import * as firebase from 'firebase';
const firebaseConfig = {
    apiKey: "AIzaSyAKrU-sA0j3OnENdOv9j26B9-AK5QB8TZw",
    authDomain: "deliveryservice2-de0dd.firebaseapp.com",
    databaseURL: "https://deliveryservice2-de0dd.firebaseio.com",
    projectId: "deliveryservice2-de0dd",
    storageBucket: "deliveryservice2-de0dd.appspot.com",
    messagingSenderId: "735856621477"
};
const firebaseApp = firebase.initializeApp(firebaseConfig);
export {firebaseApp};