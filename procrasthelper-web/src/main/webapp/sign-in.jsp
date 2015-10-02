<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html lang="en-US">
<head>
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.4.0/css/font-awesome.min.css">
  <script src="https://apis.google.com/js/platform.js" async defer></script>
  <meta name="google-signin-client_id" content="863921666066-u5u5itr52t2tb1jprcanvg4i8v6g2naa.apps.googleusercontent.com">
  <title>Login</title>
  <script src="https://apis.google.com/js/api:client.js"></script>
  <script>
    var googleUser = {};
    var startApp = function() {
      gapi.load('auth2', function(){
        // Retrieve the singleton for the GoogleAuth library and set up the client.
        auth2 = gapi.auth2.init({
          client_id: '863921666066-u5u5itr52t2tb1jprcanvg4i8v6g2naa.apps.googleusercontent.com',
          cookiepolicy: 'single_host_origin',
          // Request scopes in addition to 'profile' and 'email'
          scope: 'https://www.googleapis.com/auth/plus.login https://www.googleapis.com/auth/plus.me https://www.googleapis.com/auth/userinfo.profile'
        });
        attachSignin(document.getElementById('customBtn'));
      });
    };

    function attachSignin(element) {
      console.log(element.id);
      auth2.attachClickHandler(element, {},
              function(googleUser) {
                console.log('user authenticated with Google+');
                onSuccess(googleUser);
              }, function(error) {
                alert(JSON.stringify(error, undefined, 2));
              });
    }
  </script>
</head>
<body>
<div class="login">
  <h1>Procrastination Helper Login</h1>
  <c:url value="/login" var="loginUrl" />
  <form action="${loginUrl}" name="f" method="post">
    <input type="hidden" name="username" value="" placeholder="Username" required="required" />
    <input type="hidden" name="password" placeholder="Password" required="required" />
    <%--<input type="submit" value="Let me in." class="btn btn-primary btn-block btn-large">--%>
  </form>
  <div class="button"><a href="#" class="btn"><i class="fa fa-google-plus"></i><span id="customBtn" >Login with Google+</span></a></div>
</div>
<script>

  startApp();

  function onSuccess(googleUser) {
    var authResponse = googleUser.getAuthResponse();
    var accessToken = authResponse.access_token;
    if (accessToken == undefined) {
      return;
    }
    var profile = googleUser.getBasicProfile();
    console.log('ID: ' + profile.getId()); // Do not send to your backend! Use an ID token instead.
    console.log('Name: ' + profile.getName());
    console.log('Image URL: ' + profile.getImageUrl());
    console.log('Email: ' + profile.getEmail());
    var authResponse = googleUser.getAuthResponse();
    var accessToken = authResponse.access_token;
    if (accessToken == undefined) {
      return;
    }
    var id_token = googleUser.getAuthResponse().id_token;
    var xhr = new XMLHttpRequest();
    xhr.open('POST', 'rest/auth/google/tokensignin');
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.onload = function() {
      console.log('Signed in as: ' + xhr.responseText);
    };

    var requestJson = { idToken: id_token, accessToken: accessToken, userId: profile.getId() };
    xhr.onreadystatechange = function() {
      if (xhr.readyState == 4) {
        console.log('state changed');
        console.log('response text:' + xhr.responseText);
        var userId = JSON.parse(this.responseText).name;
        var userPassword = JSON.parse(this.responseText).password;
        document.f.username.value =userId;
        document.f.password.value =userPassword;
        console.log("username: " + userId);
        console.log("password: " + userPassword);
        document.f.submit();
      }
    };

    xhr.send(JSON.stringify(requestJson));

  }
</script>
</body>
</html>