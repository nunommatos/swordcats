# SwordCats

## App Description
The app contains all the required features from the challenge but the implemented design is a bit different from the suggested one.
I tried to display all the required information and a few extras in a way that the UI/UX was optimized by its maximum.

The app can be divided in the following screens/features:

### Splash Screen
This screen has no visible UI and its responsible for handling the current login state to determine if the app should open the login screen or the homepage.

### Login Screen
This is a very simple login/register page just for proof of concept and to support the multiple users feature that the app has.

First, you need to type your email and the app will check if that email exists. If so, then you're redirected to the home page. If not, then a new input field replaces the first one so you can type your name and create an user. If you wish to go back and try to login again, you just need to press the 'Cancel' button on the bottom of the screen.

After either completing the user creation or logging in to an existing one, you're redirected to the home page.

> **Note:**
> -I didn't include any validation for the email format, so feel free to type any text.

### Home Screen
In this screen you'll find a list of the cats retrieved from the server. Each item of the list contains the cat photo, the cat breed name and its favorite state. If you scroll until the end, a new request will be made in order to get the next page of results. You can click on any item and you'll be redirected to the clicked cat breed's details.
There are also two tabs: one for all the cat breeds and the other one for the favorite ones. The favorites also appear in the "All" tab. Finally, there's a searching field that will filter the selected tab's current list by its name. The searching method is basically checking if the typed text is contained by the breed name, ignoring if it's lower or upper cased.

> **A few notes:**
> -It's currently fetching the cats alphabetically instead of randomly so it's easy to test the favorite/unfavorite feature;
> -From reading the challenge, I was under the impression that the cat breed's lifespan should only be displayed if the item was set as favorite, so I did it like that, i.e., if it's favorite then the label appears, if not it disappears.
> -I'm not sure if I misunderstood how the API works or if it's an API limitation, but I wasn't able to get the cat breed's favorite state from the API after updating it. However, I was able to obtain it from the 'favourites endpoint' so I had to merge both requests in order to be aware of the user's favorite cats list.

### Cat Details Screen
This screen contains some information about the cat: name, favorite state, description, origin, average lifespan, average weight, temperament and a button that opens (externally, just for proof of concept) an url with the Wikipedia page of that cat breed, if available.

It's possible to update the cat favorite state in this screen and it'll automatically update the cat's favorite state on the previous screen.

### Unit Tests
There are some unit tests to cover the existing UseCases and some for the ViewModels too.