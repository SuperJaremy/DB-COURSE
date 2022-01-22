function switchOptions(toOption){
    const options = document.getElementsByClassName("option")
    for (let i = 0; i < options.length; i++) {
        options[i].style.display = "none";
    }
    document.getElementById(toOption).style.display = "block";
}