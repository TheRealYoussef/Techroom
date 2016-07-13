<?php
	header('Content-Type: application/json');
	//Get JSON in the form of
	//{"LessonID" : lesson id}
	//return 
	//[
	//	{
	//		"Status" : 200, 
	//		"Students" :
	//			[
	//				{
	//					"Name" : "student name",
	//					"Connected" : 0 or 1,
	//					"Muted" : 0 or 1,
	//					"Username" : "student username"
	//				},
	//				{
	//					"Name" : "student name",
	//					"Connected" : 0 or 1,
	//					"Muted" : 0 or 1,
	//					"Username" : "student username"
	//				},
	//				...
	//			]
	//	}
	//]
	//OR
	//[{'Status' : 409, 'Message' : 'error message'}]
	$json = file_get_contents('php://input');
	$decodedJSON = json_decode($json);
	$conn = mysqli_connect("localhost", "root", "TechroomPass1234", "Techroom");
	if (!$conn) {
   	 	echo "[{'Status' : 409, 'Message' : 'Failed to connect to database'}]";
   	 	exit;
	}
	
	$ret = array();
	$mainObj = array("Status" => 200);
	$students = array();
	
	$query = "SELECT Student, Connected, Muted FROM StudentLesson WHERE Lesson = $decodedJSON->LessonID";
	$result = mysqli_query($conn, $query);
	while ($obj = mysqli_fetch_object($result)) {
		$studentID = $obj->Student;
		$connected = $obj->Connected;
		$muted = $obj->Muted;
		$query = "SELECT Name, Username From Student WHERE ID = $studentID";
		$result2 = mysqli_query($conn, $query);
		$obj = mysqli_fetch_object($result2);
		$name = $obj->Name;
		$username = $obj->Username;
		$student = array("Name" => $name, "Connected" => $connected, "Muted" => $muted, "Username" => $username);
		array_push($students, $student);
	}
	$mainObj["Students"] = $students;
	array_push($ret, $mainObj);
	
	echo json_encode($ret);
	exit;
?>