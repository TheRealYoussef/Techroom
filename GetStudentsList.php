<?php
	header('Content-Type: application/json');
	//Get JSON in the form of
	//{"SchoolName" : "Name of school", "Country" : "School's Country"};
	//return
	//[{"Status" : 409, "Message" : "error"}] OR [{"Status" : 200, "Students" : [{"Name" : "student name", "Image" : "student image", "Orientation" : orientation}, {"Name" : "student name", "Image" : "student image", "Orientation" : orientation}, ...]}]
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
	
	$ret = array();
	$mainArr = array("Status" => 200);
	$students = array();
	$query = "SELECT Name, Image, ID, Orientation FROM Student WHERE School = $schoolID";
	$result = mysqli_query($conn, $query);
	mysqli_close($conn);
	while ($obj = mysqli_fetch_object($result)) {
		$name = $obj->Name;
		$image = "";
		if ($obj->Image == 1) {
			$id = $obj->ID;
			$image = "StudentsUploadedImages/$id.png";
		}
		$orientation = $obj->Orientation;
		$student = array("Name" => $name, "Image" => $image, "Orientation" => $orientation);
		array_push($students, $student);
	}
	
	$mainArr["Students"] = $students;
	array_push($ret, $mainArr);
	echo json_encode($ret);
	exit;
?>