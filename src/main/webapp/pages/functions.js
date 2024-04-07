function login() {

    var username = document.getElementById("username").value;
    var password = document.getElementById("password").value;

    if(username === "" || password === "") {
        alert("To login you need to fill the fields");
        return;
    }

    var xhr = new XMLHttpRequest();

    xhr.open('POST', '/rest/login', true);
    xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8"); 
    xhr.onreadystatechange = function () {
        if(xhr.readyState === 4 ) {
            if(xhr.status === 200){
                var cookie = xhr.responseText;
                alert("Login sucessfull here is you token: " + cookie);
                window.location.href = "/pages/welcome.html";
            }else {
                alert("Login failed");
            }
        }
    };  
    
    console.log(JSON.stringify(document.cookie));
    
    var dataToSend = JSON.stringify(
        {
            username: username, 
            password: password
        }
        );  
    xhr.send(dataToSend);
}

function welcome() {
    var cookie = document.cookie.split(".");
    var userRole = cookie[2];
    
    const output = document.getElementById("role");
    output.textContent = userRole;

    var xhr = new XMLHttpRequest();

    xhr.open('POST', '/rest/welcome', true);
    xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8"); 
    xhr.onreadystatechange = function () {
        if(xhr.readyState === 4 ) {
            if(xhr.status === 200){
                
                var cookie = xhr.responseText;
                var cookieSplit = cookie.split(".");

                var userRole = cookieSplit[2];
                const output = document.getElementById("role");
                output.textContent = userRole;
                
                var username = cookieSplit[0];
                const usernameOutput = document.getElementById("welcome");
                usernameOutput.innerHTML += username + "!";

            }else {
                alert("Welcome failed");
            }
        }
    };  
    xhr.send();
}

function register() {

    var fotoString = "";

    var username = document.getElementById("username").value;
    var password = document.getElementById("password").value;
    var confpassword = document.getElementById("confpassword").value;
    var email = document.getElementById("email").value;
    var phone = document.getElementById("phone").value;
    var name = document.getElementById("name").value;
    var options = document.getElementById("profileOpt").value;
    var ocupation = document.getElementById("ocupation").value;
    var workplace = document.getElementById("workplace").value;
    var household = document.getElementById("household").value;
    var postalcode = document.getElementById("postalcode").value;
    var nif = document.getElementById("nif").value;
    var foto = document.getElementById("foto").files[0];

    if(username === "" || password === "" || name === "" || phone ==="" ||  email === "") {
        alert("The elements with * are mandatory to be filled");
        return;
    }

    if(password != confpassword) {
        alert("Password and his confirmation dont match");
        return;
    }


    var dataToSend = {
        username: username,
        password: password,
        confirmation: confpassword,
        email: email,
        name: name,
        phone: phone,
        profile: options,
        ocupation: ocupation,
        workPlace: workplace,
        houseHold: household,
        CP: postalcode,
        NIF: nif
    }

    if(!foto) {
        dataToSend.foto = fotoString;
        sendRegisterRequest(dataToSend);

    }else {
        
        var reader = new FileReader();

        reader.onload = function(event) {  
            fotoString = event.target.result;
            dataToSend.foto = fotoString;
            sendRegisterRequest(dataToSend);
        }

        reader.readAsDataURL(foto);
    }
}

function sendRegisterRequest(dataToSend) {
    var xhr = new XMLHttpRequest();

    xhr.open('POST', '/rest/register', true);
    xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8"); 
    xhr.onreadystatechange = function () {
        if(xhr.readyState === 4 ) {
            if(xhr.status === 200){
                alert("Registration completed successfully.");
                window.location.href = "/pages/login.html";   
            }else {
                alert(xhr.responseText);
            }
        }
    }; 
    xhr.send(JSON.stringify(dataToSend)); 

}


function changeRole() {

    var username = document.getElementById("username").value;
    var role = document.getElementById("role").value;

    if(username === "" ) {
        alert("You need to choose a user to change the role");
        return;
    }

    if(role === "") {
        alert("You need to choose a role");
        return;
    }

    var xhr = new XMLHttpRequest();

    xhr.open('POST', '/rest/change/role', true);
    xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8"); 
    xhr.onreadystatechange = function () {
        if(xhr.readyState === 4 ) {
            if(xhr.status === 200){
                alert("Role changed");
                window.location.href = "/pages/welcome.html";   
            }else if(xhr.status === 410){
                alert(xhr.responseText);
                window.location.href = "/pages/login.html";
            }else if(xhr.status ===500){
                window.location.href = "/pages/login.html";
            }else {
                alert(xhr.responseText);
            }
        }
    };                      
    
    var dataToSend = JSON.stringify(
        {
            username: username, 
            role: role
        }
        );  
    xhr.send(dataToSend);
}


function changeState() {

    var username = document.getElementById("username").value;
    var state = document.getElementById("state").value;

    if(username === "" ) {
        alert("You need to indentify a user");
        return;
    }

    if(state === "") {
        alert("You need to choose a state");
        return;
    }

    var xhr = new XMLHttpRequest();
    xhr.open('POST', '/rest/change/state', true);
    xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8"); 
    xhr.onreadystatechange = function () {
        if(xhr.readyState === 4 ) {
            if(xhr.status === 200){
                alert(xhr.responseText);
                window.location.href = "/pages/welcome.html";   
            }else if(xhr.status === 410){
                alert(xhr.responseText);
                window.location.href = "/pages/login.html";
            }else if(xhr.status ===500){
                window.location.href = "/pages/login.html";
            }else {
                alert(xhr.responseText);
            }
        }
    };                      
    
    var dataToSend = JSON.stringify(
        {
            username: username, 
            state: state
        }
        );  
    xhr.send(dataToSend);
}

function deleteUser() {

    var username = document.getElementById("username").value;

    if(username === "" ) {
        alert("You need to indentify a user to be deleted");
    }

    var xhr = new XMLHttpRequest();
    xhr.open('POST', '/rest/change/state', true);
    xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8"); 
    xhr.onreadystatechange = function () {
        if(xhr.readyState === 4 ) {
            if(xhr.status === 200){
                alert(xhr.responseText);
                window.location.href = "/pages/welcome.html";   
            }else if(xhr.status === 410){
                alert(xhr.responseText);
                window.location.href = "/pages/login.html";
            }else if(xhr.status ===500){
                window.location.href = "/pages/login.html";
            }else {
                alert(xhr.responseText);
            }
        }
    };                      
    
    var dataToSend = JSON.stringify(
        {
            username: username
        }
        );  
    xhr.send(dataToSend);
}

function changePassword() {

    var lastPassword = document.getElementById("password").value;
    var newPassword = document.getElementById("newPassword").value;
    var newPassconf = document.getElementById("newPassConf").value;

    if(lastPassword === "" || newPassword ==="" || newPassconf ==="" ) {
        alert("You need to fill all the blanks");
        return;
    }

    if(newPassword === lastPassword) {
        alert("Your new password is the same that the actual one");
        return;
    }

    if(newPassconf != newPassword) {
        alert("The new password and his confirmation dont match");
        return;
    }

    var xhr = new XMLHttpRequest();
    xhr.open('POST', '/rest/change/password', true);
    xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8"); 
    xhr.onreadystatechange = function () {
        if(xhr.readyState === 4 ) {
            if(xhr.status === 200){
                alert("State changed");
                window.location.href = "/pages/welcome.html";
            }else if(xhr.status === 410){
                alert(xhr.responseText);
                window.location.href = "/pages/login.html";
            }else if(xhr.status ===500){
                window.location.href = "/pages/login.html";   
            }else {
                alert("State change failed");
            }
        }
    };                      
    
    var dataToSend = JSON.stringify(
        {
            actualPassword: lastPassword, 
            newPassword: newPassword,
            newPasswordConfirmation: newPassconf
        }
        );  
    xhr.send(dataToSend);
}

function logout() {
    var xhr = new XMLHttpRequest();
    xhr.open('POST', '/rest/logout', true);
    xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8"); 
    xhr.onreadystatechange = function () {
        if(xhr.readyState === 4 ) {
            if(xhr.status === 200){
                alert("Logged out");
                window.location.href = "/pages/login.html";   
            }else {
                alert("Log out failed");
            }
        }
    };
    xhr.send();                      
}

function changeAttributes() {

    var fotoString = "";
    
    var username = document.getElementById("username").value;
    var password = document.getElementById("password").value;
    var email = document.getElementById("email").value;
    var phone = document.getElementById("phone").value;
    var name = document.getElementById("name").value;
    var options = document.getElementById("options").value;
    var ocupation = document.getElementById("ocupation").value;
    var workplace = document.getElementById("workplace").value;
    var household = document.getElementById("household").value;
    var postalcode = document.getElementById("postalcode").value;
    var nif = document.getElementById("nif").value;
    var foto = document.getElementById("foto").files[0];
    var state = document.getElementById("state").value;
    var role = document.getElementById("role").value;

   if(username === "") {
    alert("you need to select a user to change his information");
    return;
   }


   var dataToSend = {
        username: username,
        password: password,
        email: email,
        name: name,
        phone: phone,
        profile: options,
        ocupation: ocupation,
        workPlace: workplace,
        houseHold: household,
        CP: postalcode,
        NIF: nif,
        state: state,
        role: role
    }
    
    if(!foto){
        dataToSend.foto = fotoString;
        sendChangeAttRequest(dataToSend);
    }else {
        var reader = new FileReader();

        reader.onload = function(event) {  
            fotoString = event.target.result;
            dataToSend.foto = fotoString;
            sendChangeAttRequest(dataToSend);
            
        }
        
        reader.readAsDataURL(foto);
    }  
}

function sendChangeAttRequest(dataToSend) {

    var xhr = new XMLHttpRequest();

    xhr.open('POST', '/rest/change/attribute', true);
    xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8"); 
    xhr.onreadystatechange = function () {
        if(xhr.readyState === 4 ) {
            if(xhr.status === 200){
                alert("Attributes changed sucessfully ");
                window.location.href = "/pages/welcome.html";  
            }else if(xhr.status === 410){
                alert(xhr.responseText);
                window.location.href = "/pages/login.html";
            }else {
                alert(xhr.responseText);
            }
        }
    }; 

    xhr.send(JSON.stringify(dataToSend));
}






function list() {
    var xhr = new XMLHttpRequest();

    xhr.open('POST', '/rest/list', true);
    xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8"); 
    xhr.onreadystatechange = function () {
        if(xhr.readyState === 4 ) {
            if(xhr.status === 200){
                
                var usersList = JSON.parse(this.responseText);
                var tbody = document.getElementById("tbody");
                var thead = document.getElementById("thead");
                var htmlb = "";
                var htmla = "";

                if(usersList.length === 0) {
                    alert("No users to list here");
                    return;
                }
                
                var user = usersList[0];
                if( user.length != 3) {

                    htmla += "<th>Phone_number</th>";
                    htmla += "<th>Password</th>";
                    // htmla += "<th>Email</th>";
                    htmla += "<th>Profile</th>";
                    htmla += "<th>Ocupation</th>";
                    htmla += "<th>Work_Place</th>";
                    htmla += "<th>Household</th>";
                    htmla += "<th>NIF</th>";
                    htmla += "<th>Codigo_Postal</th>";
                    htmla += "<th>Foto</th>";
                    htmla += "<th>Role</th>";
                    htmla += "<th>State</th>";
                    
                    for(var i = 0; i < usersList.length; i++) {
                        var userAtts = usersList[i];
                        
                        htmlb += "<tr>";
                        htmlb += "<td>" + userAtts[0] + "</td>";
                        htmlb += "<td>" + userAtts[1] + "</td>";
                        htmlb += "<td>" + userAtts[2] + "</td>";
                        htmlb += "<td>" + userAtts[3]+ "</td>";
                        htmlb += "<td>" + userAtts[4] + "</td>";
                        htmlb += "<td>" + userAtts[5] + "</td>";
                        htmlb += "<td>" + userAtts[6] + "</td>";
                        htmlb += "<td>" + userAtts[7] + "</td>";
                        htmlb += "<td>" + userAtts[8] + "</td>";
                        htmlb += "<td>" + userAtts[9] + "</td>";
                        htmlb += "<td>" + userAtts[10] + "</td>";
                        htmlb += "<td><img src='" + userAtts[11] + "' alt='User Image' style='max-width: 100px; max-height: 100px;'></td>";
                        // htmlb += "<td>" + userAtts[11] + "</td>";
                        htmlb += "<td>" + userAtts[12] + "</td>";
                        htmlb += "<td>" + userAtts[13] + "</td>"; //14 é o do state tenho que ver o que é o 11
                        htmlb += "</tr>";    
                    }
                }else {
                    for(var i = 0; i < usersList.length; i++) {
                        var userAtts = usersList[i];
                        htmlb += "<tr>";
                            htmlb += "<td>" + userAtts[0] + "</td>";
                            htmlb += "<td>" + userAtts[1] + "</td>";
                            htmlb += "<td>" + userAtts[2] + "</td>";
                        htmlb += "</tr>";
                    }
                }
                tbody.innerHTML = htmlb;
                thead.innerHTML += htmla;
            
            }else if(xhr.status === 410){
                alert(xhr.responseText);
                window.location.href = "/pages/login.html";
            }else {
                alert("Attributes change failed");
            }
        }
    };
    
    xhr.send();
}