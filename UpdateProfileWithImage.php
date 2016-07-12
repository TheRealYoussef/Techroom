<?php
	header('Content-Type: application/json');
	//Get JSON in the form of
	//{"Username" : "username", "Password" : "password", SchoolName" : "School name", "Country" : "Country name", "Name" : "name", "Image" : "encoded image", "Orientation" : image orientation}
	//return [{'Status' : 200}] or[{'Status' : 409, 'Message' : 'error message'}]
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
	
	$encodedImage = $decodedJSON->Image;
	$orientation = $decodedJSON->Orientation;
	$name = $decodedJSON->Name;
	$query = "SELECT ID, Image FROM Student WHERE Username = '$decodedJSON->Username' AND Password = '$decodedJSON->Password' AND School = $schoolID";
	$result = mysqli_query($conn, $query);
	if (mysqli_num_rows($result) != 0) {
		$obj = mysqli_fetch_object($result);
		$id = $obj->ID;
		$image = $obj->Image;
		$fileName = "StudentsUploadedImages/$id.png";
		if ($encodedImage != "") {
			$binary = base64_decode($encodedImage);
			if ($image == 0) {
				$query = "UPDATE Student set Image = 1, Orientation = $orientation, Name = '$name' WHERE ID = $id";
				mysqli_query($conn, $query);
			}
			else {
				unlink($fileName);
				$query = "UPDATE Student set Orientation = $orientation, Name = '$name' WHERE ID = $id";
				mysqli_query($conn, $query);
			}
			mysqli_close($conn);		
			$file = fopen($fileName, 'wb');
			fwrite($file, $binary);
			fclose($file);
		}
		else if ($image == 1) {
			unlink($fileName);
			$query = "UPDATE Student set Image = 0, Orientation = 1, Name = '$name' WHERE ID = $id";
			mysqli_query($conn, $query);
		}
	}
	else {
		$query = "SELECT ID, Image FROM Teacher WHERE Username = '$decodedJSON->Username' AND Password = '$decodedJSON->Password' AND School = $schoolID";
		$result = mysqli_query($conn, $query);
		if (mysqli_num_rows($result) != 0) {
			$obj = mysqli_fetch_object($result);
			$id = $obj->ID;
			$image = $obj->Image;
			$fileName = "TeachersUploadedImages/$id.png";
			if ($encodedImage != "") {
				$binary = base64_decode($encodedImage);
				if ($image == 0) {
					$query = "UPDATE Teacher set Image = 1, Orientation = $orientation, Name = '$name' WHERE ID = $id";
					mysqli_query($conn, $query);
				}
				else {
					unlink($fileName);
					$query = "UPDATE Teacher set Orientation = $orientation, Name = '$name' WHERE ID = $id";
					mysqli_query($conn, $query);
				}
				mysqli_close($conn);
				$file = fopen($fileName, 'wb');
				fwrite($file, $binary);
				fclose($file);
			}
			else if ($image == 1) {
				unlink($fileName);
				$query = "UPDATE Teacher set Image = 0, Orientation = 1, Name = '$name' WHERE ID = $id";
				mysqli_query($conn, $query);
				mysqli_close($conn);
			}
		}
		else {
			mysqli_close($conn);
			echo "[{'Status' : 409, 'Message' : 'No username $decodedJSON->Username exists in the school $decodedJSON->SchoolName'}]";
			exit;
		}
	}
	
	echo "[{'Status' : 200}]";
	exit;
?>