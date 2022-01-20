function switchOptions(option){
    const options = document.getElementsByClassName("option")
    for (let i = 0; i < options.length; i++) {
        options[i].style.display = "none";
    }
    document.getElementById(option).style.display = "block";
}