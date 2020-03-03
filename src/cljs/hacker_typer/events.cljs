(ns hacker-typer.events
  (:require
   [re-frame.core :as re-frame]
   [hacker-typer.db :as db]
   [day8.re-frame.http-fx]
   [ajax.core :as ajax]
   [clojure.string :refer [split]]
   [secretary.core]))


(defn take-rec [list n]
  (loop [acc [(take n list)]
         rest-of-list (drop n list)]
    (if (empty? rest-of-list)
      acc
      (recur (concat acc [(take n rest-of-list)]) (drop n rest-of-list)))))


(defn into-sections [string max-lines]
  (map #(clojure.string/join "\n" %)
       (take-rec (clojure.string/split-lines string) max-lines)))


(re-frame/reg-event-db
 :show-page
 (fn [db [_ new-page]]
   (assoc db :current-page new-page)))


(re-frame/reg-event-db
 :hero/repo-button-clicked
 #(assoc %
         :hero/input-hidden false
         :hero/repo-btn-hidden true
         :hero/file-btn-hidden true
         :hero/handchosen-btn-hidden true))


(re-frame/reg-event-db
 :success-get-file-content
 (fn [db [_ url resp]]
   (set! js/window.location.hash (str "#/repo" (last (split url "https://api.github.com/repos"))))
   (assoc db
          :current-page :code-page
          :typing/char-index 0
          ; :typing/file-content (js/atob (:content resp))

          :typing/sections (into-sections (js/atob (:content resp)) 30)
          :typing/section-index 0

          :typing/last-start-time (js/Date.now)
          :typing/wpm 0)))


(re-frame/reg-event-fx
 :get-file
 (fn [db [_ owner name sha]]
   (let [url (str "https://api.github.com/repos/" owner "/" name "/git/blobs/" sha)]
     {:db         db
      :http-xhrio {:method          :get
                   :uri             url
                   :timeout         8000
                   :response-format (ajax/json-response-format {:keywords? true})
                   :on-success      [:success-get-file-content url]}})))


(re-frame/reg-event-fx
 :repo-nav-page/item-clicked
 (fn [db [_ url]]
   {:db         db
    :http-xhrio {:method          :get
                 :uri             url
                 :timeout         8000
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [:success-get-file-content url]}}))


(re-frame/reg-event-fx
 :success-get-repo
 (fn [db [_ owner repo-name resp]]
   (let [repo-nav-items (filterv (fn [x] (= (:type x) "blob")) (:tree resp))
         repo-nav-dirs  (filterv (fn [x] (= (:type x) "tree")) (:tree resp))]
     (set! js/window.location.hash (str "#/repo/" owner "/" repo-name))
     {:db (assoc db
                 :typing/input-hidden true
                 :current-page :repo-nav-page
                 :repo/nav-items repo-nav-items
                 :repo/nav-dirs repo-nav-dirs)})))


(re-frame/reg-event-fx
 :get-repo
 (fn [db [_ owner name]]
   {:db         db
    :http-xhrio {:method          :get
                 :uri             (str "https://api.github.com/repos/" owner "/" name "/git/trees/master?recursive=1")
                 :timeout         8000
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [:success-get-repo owner name]
                 :on-failure      [:failure-get-repo]}}))


(re-frame/reg-event-fx
 :hero/input-enter-pressed?
 (fn [{:keys [db]} [_ key]]
   (let [new-db
         (let [[repo-owner repo-name] (split (.-value (js/document.getElementById "repo-or-file-input")) #"/")]
           (if (= key "Enter")
             {:db (assoc db :repo/owner repo-owner
                         :repo/name repo-name
                         :hero/input-hidden true) ; So that doesn't appear when going back (TODO: DOESN'T WORK)
              :dispatch [:get-repo repo-owner repo-name]}
             {:db db}))]
     new-db)))


(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))
