window.onload = function () {
    var perfil = document.getElementById("tipoPerfil").value;
    var tipoDocente = document.getElementById("selectTipoDocente").value;

    if (perfil === "DOCENTE") {
        $("#tipoDocente").show();
        if (tipoDocente === "TITULAR") {
            $("#salita").show();
        } else {
            $("#salita").hide();
            $("#selectSalitaId").val("");
        }
    } else {
        $("#tipoDocente").hide();
        $("#selectTipoDocente").val("");
        $("#salita").hide();
        $("#selectSalitaId").val("");
    }
};

function ocultarTipoDocente() {
    var perfil = document.getElementById("tipoPerfil").value;
    if (perfil === "DOCENTE") {
        $("#tipoDocente").show();
    } else {
        $("#tipoDocente").hide();
        $("#selectTipoDocente").val("");
        $("#salita").hide();
        $("#selectSalitaId").val("");
    }
}
function ocultarSalita() {//muestro el campo de salita solo si el perfil seleccionado es docente titular
    var tipoDocente = document.getElementById("selectTipoDocente").value;
    if (tipoDocente === "TITULAR") {
        $("#salita").show();
    } else {
        $("#salita").hide();
        $("#selectSalitaId").val("");
    }
}

