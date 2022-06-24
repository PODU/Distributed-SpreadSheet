<h1>Distributed SpreadSheet</h1>

<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;This is a Dual Server and Multi Client System that can operate in Distributed Format as It maintains, the properties of a Distributed System ie. Easy Scalability, Failure Handeling, Backup Protocols, etc.</p>
<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;This System is implemented using the JAVA Client-Server methodology. In it We hava connected the Application to a MySQL Server for handaling the Login and SignUp System also the File Access Privilage Level.</p>

<h2>Login & SignUP</h2>
<img src="ScreenShots/7.png" alt="LOGIN" style="width:700px;">
<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;This screen is the Login Screen through Which you can login into the system and access the files created by you or the others according to the Privilage Level.</p>
<img src="ScreenShots/1.png" alt="SIGNUP" style="width:700px;">
<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;This screen is the Sign Up Screen through Which new User can Signup into the system and create new Files with Privilage Level.</p>

<h2>Creating a New File</h2>
<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;For creating a new file first the user must Login using his/her Username and Password.</p>
<img src="ScreenShots/6.png" alt="CREATE NEW FILE" style="width:700px;">
<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;For creating a File as in the above Image the User must give it a name (No need for Extension as by default extension of .csv will be added to the File Name).</p>
<h4>Privilage Level</h4>
<ul>
<li>Private:- Only the Creator of the File can View and Edit the File.</li>
<li>Public & Can View:- Only the Creator of the File can Edit but the Other Members can View the File Contents.</li>
<li>Public & Can Edit:- The Creator of the File and the Other Users all can View and Edit the File.</li>
</ul>
<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Based of the Privilage Level and the Ownership of the File the List of Files will be accordingly displayed to different Users.</p>
<img src="ScreenShots/5.png" alt="FILE LIST DISPLAY 1" style="width:700px;">
<img src="ScreenShots/3.png" alt="FILE LIST DISPLAY 2" style="width:700px;">

<h2>SpreadSheet</h2>
<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The SpreadSheet is a 100x100 grid that will be displayed of the Screen, in the backgroung the contents of the cell will be stored in a CSV file.</p>
<img src="ScreenShots/4.png" alt="SPREADSHEET" style="width:700px;">
<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The Contents of the SpreadSheet is Updated in the Original File in Real Time so any alteration in the file done by one User will be reflected on the Display of all Other Users in Real Time.</p>
<img src="ScreenShots/2.png" alt="SPREADSHEET" style="width:700px;">
