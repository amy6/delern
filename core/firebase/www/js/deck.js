var userId;

firebase.auth().onAuthStateChanged(function(user) {
  if (user) {
    var database = firebase.database();
    userId = firebase.auth().currentUser.uid;
    firebase.database().ref('/decks/' + userId).on('value', function(snapshot) {
      var decks = snapshot.val();
      var div = document.getElementById("decks");
      div.innerHTML = "";
      for (var deckId in decks) {
          var btn = document.createElement("option"); // Create a <button> element
          btn.setAttribute("value", deckId);
          var t = document.createTextNode(decks[deckId].name); // Create a text node
          btn.appendChild(t);                                // Append the text to <button>
          div.appendChild(btn);  
        }

      });
  } else {
    window.location.href = "/signin.html"; 
  }
});

add = function(e) {
  var decks = document.getElementById("decks");
  var deckId = decks.options[decks.selectedIndex].value;
  console.log(deckId);

  var frontTextArea = document.getElementById("front");
  var backTextArea = document.getElementById("back");

  var frontSide = frontTextArea.value;
  var backSide  = backTextArea.value;

  if (frontSide.length == 0) {
    alert("Передняя сторона карточки пуста");
    return;
  }

  var card = {
    front: frontSide,
    back: backSide,
    createdAt: firebase.database.ServerValue.TIMESTAMP,
  };

  var scheduled = {
    level: "L0",
    repeatAt: (new Date()).getTime(),
  };

  var newCardKey = firebase.database().ref().child('cards').child(deckId).push().key;

  // Write the new post's data simultaneously in the posts list and the user's post list.
  var updates = {};
  updates['/cards/' + deckId +'/' + newCardKey] = card;
  updates['/learning/' + userId + '/' + deckId + '/' + newCardKey] = scheduled;

  e.target.disabled = true;
  firebase.database().ref().update(updates).then(function() {
    e.target.disabled = false;
    frontTextArea.value ='';
    backTextArea.value = '';
    document.getElementById("front").focus();
    setTimeout(function () {
      document.getElementById('add-progress').style.display='none';
    }, 3000);
    document.getElementById('add-progress').style.display='inline';
  }).catch(function(err) {
    alert(err);
    e.target.disabled = false;
  });

}

signOut = function() {
  firebase.auth().signOut()
    .then(
    function() { window.location.href = "/signin.html";  }, 
    function(error) { console.error('Sign Out Error', error); });
  };