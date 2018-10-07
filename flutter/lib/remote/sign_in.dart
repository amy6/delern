import 'dart:async';

import 'package:firebase_auth/firebase_auth.dart';
import 'package:google_sign_in/google_sign_in.dart';

enum SignInProvider {
  anonymous,
  google,
}

/// If user is already signed in, do nothing (except detecting anonymous
/// provider).
/// If we have existing credential (e.g. user was signed in at the previous
/// app run), use that credential to sign in without asking the user.
Future<FirebaseUser> signInSilently() async {
  final firebaseUser = await FirebaseAuth.instance.currentUser();
  if (firebaseUser != null) {
    if (firebaseUser.isAnonymous) {
      _currentProvider = SignInProvider.anonymous;
    }
    return firebaseUser;
  }

  _currentProvider = null;

  final googleUser = await _googleSignIn.signInSilently();
  if (googleUser != null) {
    return _withGoogle(googleUser);
  }

  return null;
}

/// Sign in using a specified provider. If the user is currently signed in
/// anonymously, try to preserve uid. This will work only if the user hasn't
/// signed in with this provider before, throwing PlatformException otherwise.
Future<FirebaseUser> signIn(SignInProvider provider) async {
  switch (provider) {
    case SignInProvider.google:
      final googleUser = await _googleSignIn.signIn();
      if (googleUser == null) {
        return null;
      }
      return _withGoogle(googleUser);
    case SignInProvider.anonymous:
      return _withAnonymous();
  }

  return null;
}

Future<void> signOut() async {
  if (_currentProvider != null) {
    switch (_currentProvider) {
      case SignInProvider.google:
        await _googleSignIn.signOut();
        break;
      case SignInProvider.anonymous:
        break;
    }
  }
  _currentProvider = null;
  await FirebaseAuth.instance.signOut();
}

final GoogleSignIn _googleSignIn = GoogleSignIn();
SignInProvider _currentProvider;

Future<FirebaseUser> _withGoogle(GoogleSignInAccount a) async {
  final googleAuth = await a.authentication;
  FirebaseUser user;

  if (_currentProvider == SignInProvider.anonymous) {
    user = await FirebaseAuth.instance.linkWithGoogleCredential(
      accessToken: googleAuth.accessToken,
      idToken: googleAuth.idToken,
    );
  } else {
    user = await FirebaseAuth.instance.signInWithGoogle(
      accessToken: googleAuth.accessToken,
      idToken: googleAuth.idToken,
    );
  }

  _currentProvider = SignInProvider.google;
  return user;
}

Future<FirebaseUser> _withAnonymous() async {
  final user = await FirebaseAuth.instance.signInAnonymously();
  _currentProvider = SignInProvider.anonymous;
  return user;
}
