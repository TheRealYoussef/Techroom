<?php
	header('Content-Type: application/json');
	//Get JSON in the form of
	//{"Username" : "student username", "SchoolName" : "School's Name", "Country" : "School's Country", "Code": "lesson code"}
	//return
	//[{"Status" : 409, "Message" : "error message"}] OR [{'Status' : 200, 'LessonID': lesson id}]
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
	
	$query = "SELECT ID FROM Student WHERE Username = '$decodedJSON->Username' AND School = $schoolID";
	$result = mysqli_query($conn, $query);
	if (mysqli_num_rows($result) == 0) {
		echo "[{'Status' : 409, 'Message' : 'No student with username $decodedJSON->Username exists in the school $decodedJSON->SchoolName'}]";
		exit;
	}
	$obj = mysqli_fetch_object($result);
	$studentID = $obj->ID;

	$query = "SELECT ID FROM Lesson WHERE Code = '$decodedJSON->Code'";
	$result = mysqli_query($conn, $query);
	if (mysqli_num_rows($result) == 0) {
		echo "[{'Status' : 409, 'Message' : 'Incorrect code'}]";
		exit;
	}
	$obj = mysqli_fetch_object($result);
	$lessonID = $obj->ID;
	
	$query = "SELECT * FROM StudentLesson WHERE Student = $studentID AND Lesson = $lessonID";				
 	$result = mysqli_query($conn, $query);
	if (mysqli_num_rows($result) == 1) {
		echo "[{'Status' : 409, 'Message' : 'Already enrolled in that lesson'}]";
		exit;
	}
	
	$query = "INSERT INTO StudentLesson VALUES ($studentID, $lessonID, 1, 0)";				
 	mysqli_query($conn, $query);
	
	mysqli_close($conn);
	echo "[{'Status' : 200, 'LessonID' : $lessonID}]";
	exit;
?>