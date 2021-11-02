# Final CS 3541

## About

This project is the final for CS 3541 Software Engineering. This is not meant to be a vague project with ambiguous requirements, so we will list them below. Important to note that there is no correct way of implementing things outside of authorization (which we will mention below). As such it is up to you to decide how you would like to implement and structure your code. **<u>Just make it work</u>**. I would suggest you think of this project as a hackathon/proof of concept project , don't sweat the small stuff and just get the grand ideas working. It will make your life much easier.

## Requirements

### Points You Need To Receive to score 100%: ***250***

Note, the maximum score is 250.

You will be implementing a simple app, TheNameGame, in which you will use a set of data about baby names to generate quiz questions for the app user.

### The app: TheNameGame

Quiz questions will come in two forms:

Form 1: OneNameMultipleYears

What year was the name Emma the most popular?

a) 1999
b) 2000
c) 2001
d) 2002
e) 2003

Form 2: MultipleNamesOneYear

Of these five names, which was the most popular in 2000?

a) Emily
b) Hannah
c) Madison
d) Ashley
e) Sarah

### Minimal Features: ***150 Points***

The basic features you need to implement are 
* A mechanism to generate a series of ten questions in Form 1 randomly from the data, shown one at a time
* A result showing whether or not the user got that question right which then moves on to the next question
* A final screen showing the user's results (how many out of 10)

### Additional Feature List  

* Authorization : (**50 Points**)
  * Oauth implementation
  * Self handling of username/passwords
* Adding question Form 2 as an option (randomly) (**20 points)
* Having a leaderboard of the top results by day for multiple users (**20 points)
* Generating questions by number of stars, stars are calculated as follows:
  Question starts with 1 star (**25 points)
  - Add two stars if the name data is drawn from 1880-1950
  - Add one star if the name data is drawn from 1951-1990
  - Add two stars if the names used are outside of the top 100 for the years in question
  - Add one stars if the names used are outside of the top 20 for the years in question
* Allowing two users to "challenge" each other, so that they get the same 10 questions, and one is declared the winner (**40 points)
  * Track those results over time (**20 points) showing results against each opponent
* Show interesting graphics on how well the user does from simply showing their results over time to more complex things (**10-50 points depending on what you show and how cool your graphics are)
  
### The fancier you do something the more points we may allot. The points you see above are minimal points given for just the functionality.
