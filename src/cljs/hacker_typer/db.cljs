(ns hacker-typer.db
  (:require [cljs.spec.alpha :as s]))

; TODO: Will this work with namespaced keys?
(s/def ::input-hidden boolean?)
(s/def ::repo-btn-hidden boolean?)
(s/def ::handchosen-btn-hidden boolean?)
(s/def ::file-btn-hidden boolean?)

(s/def ::current-page (s/keys :req-un [::hero ::repo-nav-page ::code-page ::done-typing-page]))

; TODO: Will this work with namespaced keys?
(s/def ::repo-owner string?)
(s/def ::repo-name string?)

(s/def ::file-name string?)

(s/def ::typing-file-content string?)
(s/def ::typing-char-index string?)
(s/def ::typing-wpm number?)
(s/def ::typing-typed-wrong-char boolean)

(def default-db
  {:hero/input-hidden true
   :hero/repo-btn-hidden false
   :hero/handchosen-btn-hidden false
   :hero/file-btn-hidden false
   :current-page :hero})
  ; {:hero {:repo-btn-hidden false
  ;         :file-btn-hidden false
  ;         :hc-btn-hidden false
  ;         :txtarea-hidden true
  ;  :current-page :hero
  ;  :repo-owner ""
  ;  :repo-name ""
  ;  :file-name "")

