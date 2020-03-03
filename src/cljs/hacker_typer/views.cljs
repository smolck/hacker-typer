(ns hacker-typer.views
  (:require
   [re-frame.core :as re-frame :refer [subscribe dispatch]]
   [hacker-typer.subs :as subs]
   [secretary.core]
   [hacker-typer.typing-logic :as typing-logic :refer [keydown-event-listener]]
   [clojure.string :as string]))

(defn hero []
  (let [repo-btn-hidden @(subscribe [::subs/repo-btn-hidden])
        file-btn-hidden @(subscribe [::subs/file-btn-hidden])
        hc-btn-hidden   @(subscribe [::subs/handchosen-btn-hidden])
        input-hidden    @(subscribe [::subs/hero-input-hidden])]
    [:div#hero
     [:h1 "HackerTyper"]
     [:div {:class "hero-buttons"}
      [:input#user-input
       {:id          "repo-or-file-input"
        :hidden      input-hidden
        :placeholder "repo-org-or-user/repo-name"
        :on-key-down #(dispatch [:hero/input-enter-pressed? (.-key %)])}]
      [:button#repo-btn
       {:on-click #(dispatch [:hero/repo-button-clicked])
        :hidden   repo-btn-hidden}
       "Repo"]
      [:button#file-btn {:hidden file-btn-hidden}
       "File"]
      [:button#hand-chosen-btn {:hidden hc-btn-hidden}
       "Hand Chosen"]]]))

(defn repo-nav-page [nav-items nav-dirs]
  [:div#repo-nav-uls
   (sort-by (fn [i] (count (nth i 2))) (for [dir nav-dirs]
                                         [:ul#repo-nav-ul (:path dir)
                                          (for [item (filter
                                                      #(not (nil?
                                                             (re-matches
                                                              (re-pattern (str (:path dir) "/([^/]{0,})")) (:path %))))
                                                      nav-items)]
                                            [:button#repo-nav-btn
                                             {:on-click #(dispatch [:repo-nav-page/item-clicked (:url item)])}
                                             (last (string/split (:path item) "/"))])]))])

(defn code-page [char-index section-content wpm typed-wrong-char]
  (set! js/document.onkeydown keydown-event-listener)
  (if typed-wrong-char
    (dispatch [::typing-logic/flash-background-color "current-letter" "red"]))

  [:div#code-wrapper
   [:div#wpm-counter "WPM: " wpm]
   [:pre#code-pre
    (map-indexed
     (fn [idx char]
       (if (= idx char-index)
         (if (= char "\n")
           ^{:key idx} [:span#current-letter {:class "return"} "\n"]
           ^{:key idx} [:span#current-letter char])
         (if (= char "\n")
           ^{:key idx} [:span {:class "return hidden"} "\n"]
           ^{:key idx} [:span char])))
     section-content)]])

(defn done-typing-page [wpm]
  [:div
   [:h1 "You're final WPM: " wpm]
   [:button "Type another file."]
   [:button "Choose another repo."]
   [:button "Go back to home page."]])

(defn main-panel []
  (let [current-page         @(subscribe [::subs/current-page])
        nav-items            @(subscribe [::subs/repo-nav-items])

        char-index           @(subscribe [::subs/typing-char-index])
        wpm                  @(subscribe [::subs/typing-wpm])
        typed-wrong-char     @(subscribe [::subs/typed-wrong-char])
        nav-dirs             @(subscribe [::subs/repo-nav-dirs])]
    (case current-page
      :hero (hero)
      :repo-nav-page (repo-nav-page nav-items nav-dirs)
      :code-page (code-page char-index @(subscribe [::subs/typing-current-section-content]) wpm typed-wrong-char)
      :done-typing-page (done-typing-page wpm) ; TODO: Get average WPM?
      [:div])))
