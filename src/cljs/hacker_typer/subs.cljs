(ns hacker-typer.subs
  (:require
   [re-frame.core :as re-frame]))


(re-frame/reg-sub ::hero-input-hidden     #(get % :hero/input-hidden))
(re-frame/reg-sub ::repo-btn-hidden       #(get % :hero/repo-btn-hidden))
(re-frame/reg-sub ::file-btn-hidden       #(get % :hero/file-btn-hidden))
(re-frame/reg-sub ::handchosen-btn-hidden #(get % :hero/handchosen-btn-hidden))
(re-frame/reg-sub ::current-page          #(get % :current-page))


(re-frame/reg-sub ::repo-owner     #(get % :repo/owner))
(re-frame/reg-sub ::repo-name      #(get % :repo/name))
(re-frame/reg-sub ::repo-nav-items #(get % :repo/nav-items))
(re-frame/reg-sub ::repo-nav-dirs  #(get % :repo/nav-dirs))

; (re-frame/reg-sub ::current-file-content #(get % :current-file-content))
; (re-frame/reg-sub ::current-letter-index #(get % :current-letter-index))

(re-frame/reg-sub ::typing-file-content #(get % :typing/file-content))
(re-frame/reg-sub ::typing-char-index   #(get % :typing/char-index))
(re-frame/reg-sub ::typing-wpm          #(get % :typing/wpm))
(re-frame/reg-sub ::typed-wrong-char  #(get % :typing/typed-wrong-char))
(re-frame/reg-sub ::typing-current-section-content #(nth (:typing/sections %) (:typing/section-index %)))
