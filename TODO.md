

# Code Quality

1.  Naming improvements

    Not sure exactly what names need improvement,
    but I'm sure there are multiple names that need help ;)

2.  Finish \`app-db\` spec (in <./src/cljs/hacker_typer/db.cljs>)

    After doing this need to verify the spec after every
    event handler with a re-frame interceptor.

3.  Handle DOM-related side effects with actual handlers

    As opposed to just calling \`set!\` wherever/whenever I want ;)


# Features

1.  Hook up button event handlers on "done-typing" page and pretty it up

2.  Handle \`onblur\` and \`onfocus\` window events for pausing/resuming wpm counter.

    ATM the WPM counter keeps ticking away even after the user clicks away. This should
    not be the case.

3.  Possibly be smart about breaking up the code into several sections?

    Currently I just break up files if they're greater than ~20 (?) lines into
    however many sections needed, but this can probably be improved to only break
    at the end of code blocks or something, not really sure.


# Longer-term/Bigger/More time consuming

1.  Add a backend (with Clojure and Sinatra-style probably, nothing super-fancy)

    Database for letting users login and track their progress
    Have the user be able to stop typing and come back to the same section (if it's
    a longer file) they were already at.

2.  Host the website with something (Netlify free tier?)

3.  Use browser storage (or cookies?) to save user progress (eventually, if not logged in)

    Mainly for if user is typing in a section of a longer file, can come back
    and continue typing where he left off.

4.  User dashboard showing latest things typed, progress, etc.

5.  Some sort of reward system?

    This is just a passing thought, not at all sure how this might work out,
    but could potentially be cool. Gives some sort of incentive to type more?
    
    An idea might be to have a certain amount of items to collect by typing, and
    each (main, at least at first, i.e. Python, JS/TS, etc.) programming language
    would have a certain amount of items to collect by typing. The items would have
    a random chance at appearing, and would hover over the characters as the user typed,
    possibly "running" away from the user until they typed the last character of
    the file? Lots of possibilities.


# (Known) Bugs

1.  The \`flash-background-color!\` function doesn't always function properly

        (defn flash-background-color! [element-id flash-color]
          (let [element-style (-> (js/document.getElementById element-id) .-style)
                current-color (.-backgroundColor element-style)]
            (set! (.-backgroundColor element-style) flash-color)
            (js/setTimeout (fn []
                             (set! (.-backgroundColor element-style) current-color))
    
    Problem is when this function is called multiple times in a row quickly, the \`setTimeout\` calls
    overlap and the background color ends up staying one color (usually red). For example, holding down
    the wrong key while typing will likely cause this issue to happen.
    
    Could potentially speed up the timeout to make this less likely to occur, but that's not really
    *fixing* the problem. Need to find a way to only ever have one timeout at a time, or it might
    be better to find a way to have multiple timeouts wait for the latest one to finish before running.

