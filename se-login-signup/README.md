# se-login-signup

ICSI 418Y Software Engineering — Homework Assignment 1

## Overview

A two-page web application featuring a **Login** page and a **Signup** page, built with plain HTML and CSS (no external frameworks).

## Project Structure

```
se-login-signup/
├── login.html      # Login page
├── signup.html     # Signup / registration page
├── styles.css      # Shared stylesheet for both pages
└── README.md       # This file
```

## Pages

### Login (`login.html`)
- User ID input field
- Password input field
- Submit button ("Sign In")
- Link to the Signup page

### Signup (`signup.html`)
- First Name input field
- Last Name input field
- User ID input field
- Password input field
- Submit button ("Create Account")
- Link to the Login page

## How to Run

Open either `login.html` or `signup.html` directly in any modern web browser — no build step or server required.

```bash
# Example using Python's built-in server (optional)
python3 -m http.server 8080
# Then visit http://localhost:8080/login.html
```

## Design Notes

- Both pages share a single `styles.css` file for visual consistency.
- Layout centers the form card on the page using Flexbox.
- No external CSS frameworks are used.
- Google Fonts are loaded via `@import` in `styles.css` for typography only.
- Responsive design adapts to smaller screen widths.

## Author

Noah — University at Albany (UAlbany)  
Course: ICSI 418Y Software Engineering