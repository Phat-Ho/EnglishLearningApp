<?php
    if($_SERVER['REQUEST_METHOD'] == 'GET'){
        require_once "connect.php";

        $userId = $_GET['userid'];
        $getAllHistoryQuery = "SELECT * FROM history where userID = $userId";
        $data = $conn->query($getAllHistoryQuery);
        $historyArray = array();

        while($row = $data->fetch_assoc()){
            array_push($historyArray, new History($row['id']
                                            , $row['userID']
                                            , $row['wordID']
                                            , $row['date']
                                            , $row['syncStatus']));
        }

        echo json_encode($historyArray);
    }else{
        $result["message"] = "error method";

        echo json_encode($result);
    }

    class History{
        function History($id, $userId, $wordId, $date, $syncStatus){
            $this->id = $id;
            $this->userId = $userId;
            $this->wordId = $wordId;
            $this->date = $date;
            $this->syncStatus = $syncStatus;
        }
    }
?>