<?php
    require 'init.php';
    $name = $_POST["name"];
    $city = $_POST["city"];
    $blood_group = $_POST["blood_group"];
    $password = $_POST["password"];
    $number = $_POST["number"];
  
    $check = "SELECT * FROM user_table WHERE number = '$number'";
    $response = mysqli_query($con, $check);
    
    if (mysqli_num_rows($response) > 0) {
        echo "User already exist. Please user another number.";
    } else {
         
        $sql = "INSERT INTO user_table (name, city, blood_group, password, number) VALUES('$name', '$city', '$blood_group', '$password', '$number')";
        $result = mysqli_query($con, $sql);
        echo "Registered.";
    }
    
    mysqli_close($con)
?>