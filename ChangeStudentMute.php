<?php
	header('Content-Type: application/json');
	//Get JSON in the form of
	//{"LessonID" : lesson id, "Username" : "student username", "Muted" : 0 or 1}
	//Return [{'Status' : 200}] OR [{'Status' : 409, 'Message' : 'error message'}]
	$json = file_get_contents('php://input');
	$decodedJSON = json_decode($json);
	$conn = mysqli_connect("localhost", "root", "TechroomPass1234", "Techroom");
	if (!$conn) {
   	 	echo "[{'Status' : 409, 'Message' : 'Failed to connect to database'}]";
   	 	exit;
	}
	
	$query = "SELECT ID, DeviceID FROM Student WHERE Username = '$decodedJSON->Username'";
	$result = mysqli_query($conn, $query);
	if (mysqli_num_rows($result) != 1) {
		echo "[{'Status' : 409, 'Message' : 'Student does not exist'}]";
		exit;
	}
	$obj = mysqli_fetch_object($result);
	$studentID = $obj->ID;
	$deviceID = $obj->DeviceID;
	
	$query = "UPDATE StudentLesson SET Muted = $decodedJSON->Muted WHERE Lesson = $decodedJSON->LessonID AND Student = $studentID";
	mysqli_query($conn, $query);
	
	function sendNotification($tokens, $message) {
		$url = 'https://fcm.googleapis.com/fcm/send';
		$fields = array(
			'registration_ids' 	=> $tokens,
			'data'			=> $message
			);
		$headers = array(
			'Authorization: key=AIzaSyAYNGmfAh_hHLda6bMbQFLIVOb8yrKuC6o', //Server key, obtained from firebase account (constant)
			'Content-Type: application/json'
			);
		$ch = curl_init();
		curl_setopt($ch, CURLOPT_URL, $url);
		curl_setopt($ch, CURLOPT_POST, true);
		curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
		curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
		curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, 0);
		curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
		curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
		$result = curl_exec($ch);
		curl_close($ch);
	}
	
	$tokens = array($deviceID);

	$message;
	
	if ($decodedJSON->Muted == 1) {
		$message = array(
			'ID' => 'StudentMute',
			'Message' => "You were muted in a lesson"
			);
	}
	else {
		$message = array(
			'ID' => 'StudentUnmute',
			'Message' => "You were unmuted in a lesson"
			);
	}
	
	sendNotification($tokens, $message);
	
	echo "[{'Status' : 200}]";
	exit;
?>