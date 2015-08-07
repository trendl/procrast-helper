<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html lang="en-US">
<head>
  <script src="https://apis.google.com/js/platform.js" async defer></script>
  <meta name="google-signin-client_id" content="863921666066-u5u5itr52t2tb1jprcanvg4i8v6g2naa.apps.googleusercontent.com">
  <title>Login</title>
</head>
<body>
<div class="login">
  <h1>Procrastination Helper Login</h1>
  <c:url value="/login" var="loginUrl" />
  <form action="${loginUrl}" method="post">
    <input type="text" name="username" value="" placeholder="Username" required="required" />
    <input type="password" name="password" placeholder="Password" required="required" />
    <input type="submit" value="Let me in." class="btn btn-primary btn-block btn-large">
  </form>
  <p><a href="/auth/google">Google sign in</a></p>
  <div class="g-signin2" data-onsuccess="onSignIn"></div>
</div>
<script>
  function onSignIn(googleUser) {
    var profile = googleUser.getBasicProfile();
    console.log('ID: ' + profile.getId()); // Do not send to your backend! Use an ID token instead.
    console.log('Name: ' + profile.getName());
    console.log('Image URL: ' + profile.getImageUrl());
    console.log('Email: ' + profile.getEmail());
    var authResponse = googleUser.getAuthResponse();
    var id_token = googleUser.getAuthResponse().id_token;
    console.log('id token: ' + id_token);
  }
</script>
</body>
</html>