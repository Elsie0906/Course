
<?php
	header('Access-Control-Allow-Origin: *');  
?> 

<?php if(isset($_REQUEST["q"])): ?>
	<?php
		$msg = "Welcome to my First php script";
		echo json_encode($msg);
	?>
<?php endif; ?> 	
