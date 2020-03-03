(ns hacker-typer.typing-logic
  (:require [clojure.string :as string :refer [lower-case split]]
            [re-frame.core :as re-frame :refer [dispatch]]))

(defn keydown-event-listener [e]
  (if (or (.-isComposing e) (= (.-keyCode e) 229))
    ()
    (do (if (or (= (.-key e) " ") ; To prevent moving to bottom of screen when pressing the spacebar.
                (= (.-key e) "/")) ; For preventing FireFox "Quick find" from popping up when pressing `/`.
          (.preventDefault e))
        (dispatch [::character-typed (.-key e)]))))

(defn wpm [words-typed last-start-time]
  (let [seconds (/ (- (js/Date.now) last-start-time) 1000)
        minutes (/ seconds 60)]
    (js/Math.round (/ words-typed minutes))))

(defn flash-background-color! [element-id flash-color]
  (let [element-style (-> (js/document.getElementById element-id) .-style)
        current-color (.-backgroundColor element-style)]
    (set! (.-backgroundColor element-style) flash-color)
    (js/setTimeout (fn []
                     (set! (.-backgroundColor element-style) current-color))
                   150)))

(re-frame/reg-event-db
 ::flash-background-color
 (fn [db [_ element-id color]]
   (flash-background-color! element-id color)
   (assoc db
          :typing/typed-wrong-char false)))

(defn at-end-of-section? [char-index current-section]
  (= char-index (- (count current-section) 1)))

(defn handle-end-of-section [db]
  (let [new-sect-index (inc (:typing/section-index db))
        new-section (nth (:typing/sections db) new-sect-index nil)]
    (if (not (nil? new-section))
      (assoc db :typing/section-index new-sect-index
             :typing/char-index 0)
      (assoc db :typing/section-index nil
             :current-page :done-typing-page))))

(re-frame/reg-event-db
 ::character-typed
 (fn [db [_ char]]
   (if (= (:typing/char-index db) (- (count (nth (:typing/sections db)
                                                 (:typing/section-index db))) 1))
     (handle-end-of-section db)
     (let [char-index         (:typing/char-index db)
           section-content    (nth (:typing/sections db) (:typing/section-index db))
           char-at-index      (nth section-content char-index)
           new-db-from-old    #(assoc %
                                      :typing/char-index (inc char-index)
                                      :typing/typed-wrong-char false)
           update-wpm         #(assoc % :typing/wpm (wpm (js/Math.round (/ char-index 5)) (:typing/last-start-time db)))]
       (assoc (condp = char
                char-at-index (new-db-from-old db)
                "Enter" (if (= char-at-index "\n")
                          ;; Skip indentation. Done by moving past any spaces or newlines only after pressing "Enter"
                          (let [temp-content (drop char-index section-content) ; Search only the content in front of the current char.
                                next-char-idx (:index (first
                                                       (filter #(and (not= (:char %) " ") (not= (:char %) "\n"))
                                                               (map-indexed (fn [idx char] {:index idx :char char})
                                                                            temp-content))))]
                            (assoc db :typing/char-index
                                   (+ next-char-idx char-index))) ; Add back `char-index` to account for having `drop`ed from `section-content`.
                          (assoc db :typing/typed-wrong-char true))
                "Backspace" (if (< (dec char-index) 0)
                              db
                              (assoc db :typing/char-index (dec char-index)))
                "Shift" db
                "Ctrl" db
                (assoc db :typing/typed-wrong-char true))
              :typing/wpm (wpm (js/Math.round (/ char-index 5)) (:typing/last-start-time db)))))))
