<?php
	header('Content-Type: application/json');
	//Get JSON in the form of
	//{"SchoolName" : "School Name", "Country" : "Country", Username" : "username entered"}
	//Return
	//[{"Status" : 200, "Name" : "name", "Image" : "image", "Orientation" : orientation}] OR [{"Status" : 409, "Message" : "error"}]
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
	
	$query = "SELECT Name, Image, Orientation, ID FROM Student WHERE Username = '$decodedJSON->Username' AND School = $schoolID";
	$result = mysqli_query($conn, $query);
	if (mysqli_num_rows($result) == 1) {
		mysqli_close($conn);
		$obj =  mysqli_fetch_object($result);
		$image = "";
		if ($obj->Image == 1) {
			$id = $obj->ID;
			$image = "StudentsUploadedImages/$id.png";
		}
		echo "[{'Status' : 200, 'Name' : '$obj->Name', 'Image' : '$image', 'Orientation' : $obj->Orientation}]";
		exit;
	}
	
	$query = "SELECT Name, Image, Orientation, ID FROM Teacher WHERE Username = '$decodedJSON->Username' AND School = $schoolID";
	$result = mysqli_query($conn, $query);
	if (mysqli_num_rows($result) == 1) {
		mysqli_close($conn);
		$obj =  mysqli_fetch_object($result);
		$image = "";
		if ($obj->Image == 1) {
			$id = $obj->ID;
			$image = "TeachersUploadedImages/$id.png";
		}
		echo "[{'Status' : 200, 'Name' : '$obj->Name', 'Image' : '$image', 'Orientation' : $obj->Orientation}]";
		exit;
	}
	
	mysqli_close($conn);
	echo "[{'Status' : 409, 'Message' : 'No username $decodedJSON->Username exists in the school $decodedJSON->SchoolName'}]";
	exit;
?>