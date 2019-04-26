# MediaTestUtil

## Requirements
* Show a timer with millisecond precision
* It's easy to recognize the timer via OCR module (**tesseract**)
* Query clock via socket (**TCP**)
* A gliding backgroud.
* Show IP address.

## Features
* Show epoch clock with millisecond
* Show duplicated timer values for OCR correcting
* white/black as foreground/background
* Full screen mode to cover all system area on the screen.
* A background task to reply TCP requrement with a timestamp.
* The background task can be closed by receiving 0.0d
* A gliding background (global map)
* Quit full screen mode by click, auto back to full screen mode in 3 seconds.
* A button appeared while not-full-screen model
* The text on the button is the IPv4 Address
* Click button to stop/restart TCP task
* The background task is implemented with WorkManager, it won't be killed by Android even screen-off mode