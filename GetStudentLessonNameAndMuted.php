<?php
	header('Content-Type: application/json');
	//Get JSON in the form of
	//{"Username" : "username", "SchoolName" : "School name", "Country" : "Country name", "LessonID" : lesson id}
	//return [{'Status' : 200, 'Name' : 'student name', 'Muted' : 0 or 1}] or[{'Status' : 409, 'Message' : 'error message'}]
	$json = file_get_contents('php://input');
	$decodedJSON = json_decode($json);
	$conn = mysqli_connect("localhost", "root", "TechroomPass1234", "Techroom");
	if (!$conn) {
   	 	echo "[{'Status' : 409, 'Message' : 'Failed to connect to database'}]";
   	 	exit;
	}
	
	$query = "SELECT ID FROM Country WHERE Name = '$decodedJSON->Country'";
	$result = mysqli_query($conn, $query);
	if (mysqli_num_rows($result) == 0) {
		mysqli_close($conn);
		echo "[{'Status' : 409, 'Message' : 'Country $decodedJSON->Country does not exist'}]";
		exit;
	}
	$obj = mysqli_fetch_object($result);
	$countryID = $obj->ID;

	$query = "SELECT ID FROM School WHERE Name = '$decodedJSON->SchoolName' AND Country = $countryID";
	$result = mysqli_query($conn, $query);
	if (mysqli_num_rows($result) == 0) {
		mysqli_close($conn);
		echo "[{'Status' : 409, 'Message' : 'No school $decodedJSON->SchoolName exists in the country $decodedJSON->Country'}]";
		exit;
	}
	$obj = mysqli_fetch_object($result);
	$schoolID  = $obj->ID;
	
	$query = "SELECT ID, Name FROM Student WHERE Username = '$decodedJSON->Username' AND School = $schoolID";
	$result = mysqli_query($conn, $query);
	if (mysqli_num_rows($result) == 0) {
		echo "[{'Status' : 409, 'Message' : 'No student with username $decodedJSON->Username exists in the school $decodedJSON->SchoolName'}]";
		exit;
	}
	$obj = mysqli_fetch_object($result);
	$studentID = $obj->ID;
	$name = $obj->Name;
	
	$query = "SELECT Muted FROM StudentLesson WHERE Student = $studentID AND Lesson = $decodedJSON->LessonID";
	$result = mysqli_query($conn, $query);
	if (mysqli_num_rows($result) == 0) {
		echo "[{'Status' : 409, 'Message' : 'Student not enrolled in lesson'}]";
		exit;
	}
	$obj = mysqli_fetch_object($result);
	$muted = $obj->Muted;
	
	echo "[{'Status' : 200, 'Muted' : $muted, 'Name' : '$name'}]";
	exit;
?>