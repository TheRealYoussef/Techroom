<?php
	header('Content-Type: application/json');
	//Get JSON in the form of
	//{"Username" : "teacher username", "SchoolName" : "School's Name", "Country" : "School's Country", "Title" : "lesson title", "Description" : "course desc", "CanBeRecorded" : 0 or 1}
	//return
	//[{"Status" : 409, "Message" : "Error message"}] OR [{'Status' : 200, 'Code':'$code', 'LessonID' : lessonID}]
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
	
	$query = "SELECT ID FROM Teacher WHERE Username = '$decodedJSON->Username' AND School = $schoolID";
	$result = mysqli_query($conn, $query);
	if (mysqli_num_rows($result) == 0) {
		echo "[{'Status' : 409, 'Message' : 'No teacher with username $decodedJSON->Username exists in the school $decodedJSON->SchoolName'}]";
		exit;
	}
	$obj = mysqli_fetch_object($result);
	$teacherID = $obj->ID;
	
	$query = "SELECT * FROM Lesson WHERE Name = '$decodedJSON->Title' AND Teacher = $teacherID";
	$result = mysqli_query($conn, $query);
	if (mysqli_num_rows($result) == 1) {
		echo "[{'Status' : 409, 'Message' : 'A lesson of yours already exists with the same title'}]";
		exit;
	}
	
	$code;
	$characters = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
	
	do {
		$code = "";
		for ($i = 0; $i < 5; ++$i) {
			$code = $code . $characters[rand(0, strlen($characters) - 1)];
		}
		$query = "SELECT * FROM Lesson WHERE Code = '$code'";
		$result = mysqli_query($conn, $query);
	} while (mysqli_num_rows($result) == 1);
	
	$query = "INSERT INTO Lesson (Name, Description, CanBeRecorded, Started, Ended, Code, Teacher) VALUES ('$decodedJSON->Title', '$decodedJSON->Description', $decodedJSON->CanBeRecorded, 0, 0, '$code', $teacherID)";				
 	mysqli_query($conn, $query);
  	$lessonID = mysqli_insert_id($conn);

	mysqli_close($conn);
	echo "[{'Status' : 200, 'Code':'$code', 'LessonID' :$lessonID}]";
	exit;
?>