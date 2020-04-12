<?php
    if($_SERVER['REQUEST_METHOD'] == 'POST'){
        require_once "connect.php";
        $wordID = $_POST['wordid'];
        $userID = $_POST['userid'];
        $datetime = $_POST['datetime'];
        $syncStatus = $_POST['sync'];

        $addHistoryQuery = "INSERT INTO history (userID, wordID, date, syncStatus) VALUES ('$userID'
                                            , '$wordID', '$datetime', '$syncStatus')";

        if(mysqli_query($conn, $addHistoryQuery)){
            $result["message"] = "success";

            echo json_encode($result);
            mysqli_close($conn);
        }else{
            $result["message"] = "error";

            echo json_encode($result);
            mysqli_close($conn);
        }
    }else{
        $result["message"] = "error method";

        echo json_encode($result);
    }
?>