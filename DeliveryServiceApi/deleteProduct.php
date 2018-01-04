<?php
 
/*
* Database Constants
* Make sure you are putting the values according to your database here 
*/
define('DB_HOST','localhost');
define('DB_USERNAME','root');
define('DB_PASSWORD','');
define('DB_NAME', 'android');
 
//Connecting to the database
$conn = new mysqli(DB_HOST, DB_USERNAME, DB_PASSWORD, DB_NAME);
 
//checking the successful connection
if($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}
 
//making an array to store the response
$response = array(); 
 
//if there is a post request move ahead 
if($_SERVER['REQUEST_METHOD']=='GET'){
 
 //getting the name from request 
 $id = $_GET['id']; 
 
 //creating a statement to delete from database 
 $stmt = $conn->prepare("DELETE FROM products WHERE id = '$id'");

 //if data inserts successfully
 
 if($stmt->execute()){
 //making success response 
 $response['error'] = false; 
 $response['message'] = 'Product deleted successfully'; 
 }else{
 //if not making failure response 
 $response['error'] = true; 
 $response['message'] = 'Please try later';
 }
 
}else{
 $response['error'] = true; 
 $response['message'] = "Invalid request"; 
}
 
//displaying the data in json format 
echo json_encode($response);
?>
