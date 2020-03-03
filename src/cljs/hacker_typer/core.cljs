(ns hacker-typer.core
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as re-frame]
   [hacker-typer.events :as events]
   [hacker-typer.views :as views]
   [hacker-typer.config :as config]
   [clojure.string :refer [split]]
   [secretary.core :as secretary :refer-macros [defroute]]
   [goog.events :as gevents])
  (:import
   [goog History]
   [goog.history EventType]))


(defn hook-browser-navigation! []
  (doto (History.)
    (gevents/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))


(defn routes []
  (secretary/set-config! :prefix "#")

  (defroute "/" []
    (re-frame/dispatch [:show-page :hero]))

  (defroute "/repo/:owner/:repo-name" [owner repo-name]
    (re-frame/dispatch [:get-repo owner repo-name]))

  (defroute "/repo/:owner/:repo-name/git/blobs/:sha" [owner repo-name sha]
    (re-frame/dispatch [:get-file owner repo-name sha]))

  (hook-browser-navigation!))


(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn init []
  (routes)
  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root))
