<?php
    require"init.php";
    if (isset($_POST['image'])){
        
        $target_dir = "uploads/";
        $image = $_POST['image'];
        $imageStore = rand()."_".time().".jpeg";
        $target_dir = $target_dir."/".$imageStore;
        file_put_contents($target_dir, base64_decode($image));
        
        
        $url = "https://dermic-tents.000webhostapp.com/uploads/".$imageStore;
        $message = $_POST['message'];
        $number = $_POST['number'];
         
        $sql = "INSERT INTO `posts` (`message`, `url`, `number`) VALUES ('$message', '$url', '$number');";
        $response = mysqli_query($con, $sql);
        
        if($response){
            echo "Posted";
        }else{
            echo "Failed Post";
        }
        mysqli_close($con);
    }
?>