# Vitality Recruiter

A personal Java application that automatically submits a weekly clan recruitment 
post to Reddit for Clash of Clans **Vitality**
([vitality.clashhitters.com](https://vitality.clashhitters.com)).

## What It Does

1. **Fetches live clan stats** from the Clash Hitters clan dashboard 
   (vitality.clashhitters.com) — member count, war record, CWL league, 
   trophy requirements, and join requirements
2. **Builds a formatted post** using a Markdown template stored in the 
   `resources` folder, populated with the fetched stats
3. **Submits the post** to r/ClashOfClansRecruit once per week via the 
   Reddit Data API using OAuth2 (script-type app, personal account only)

## Scope & Limitations

- Posts to **one subreddit only**: r/ClashOfClansRecruit
- Submits **one post per week** on a fixed schedule
- Makes **no comments, votes, messages, or any other API calls**
- Runs on the developer's own machine/server
- Only accesses the developer's own Reddit account

## Tech Stack

- Java 21+
- Maven (`pom.xml`)
- Reddit Data API (OAuth2, password grant, script-type app)
- User-Agent: `java:com.classhitters.vitality-recruiter:v1.0 (by /u/MysteriousRival)`

## Reddit API Compliance

This application complies with Reddit's Responsible Builder Policy:
- Registered as a **script**-type OAuth app under the developer's own account
- Clearly identified User-Agent string
- No spam, cross-posting, vote manipulation, or automated comments
- One post per week is equivalent to a human posting manually on a schedule

## Configuration

Credentials are stored in environment variables and never committed to source 
control (see `.gitignore`):