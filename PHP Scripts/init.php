<?php
    $host = "localhost"; //on same server we keep localhost
    $user = "id15635535_bloodbank";  //username of the database
    $pass = "wxc+0!oU@LtPw/!s";   //password of the database
    $db = "id15635535_blood_bank";  //name of database
    
    $con = mysqli_connect($host,$user,$pass,$db);
    
    if($con){
        //echo "Connected to Database";
    }else{
        //echo "Failed to connect ".mysqli_connect_error();
    }
?>
