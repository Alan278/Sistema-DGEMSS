function enviar(){
    var image = cropRounded();
    if(image){
        $("#foto").val(image)
    }
}

$(document).ready(function() {

    $("input[name='seleccionar']").on("change", function(e){

        var files = e.target.files;
        var done = function (url) {
            fotografiaOriginal.src = url;

            if (cropper != undefined) {
                cropper.destroy();
                cropper = null;
            }

            cropper = new Cropper(fotografiaOriginal, {
                viewMode: 3,
                dragMode: 'move',
                aspectRatio: 0,
                autoCropArea: 1,
                restore: false,
                guides: true,
                center: true,
                highlight: false,
                cropBoxMovable: false,
                cropBoxResizable: false,
                toggleDragModeOnDblclick: false,
                data:{
                    width: 8000,
                    height:  8000,
                },
            });

        }

        if (files && files.length > 0) {
            var file = files[0];

            if (URL) {
            done(URL.createObjectURL(file));
            } else if (FileReader) {
            var reader = new FileReader();
            reader.onload = function (e) {
                done(reader.result);
            };
            reader.readAsDataURL(file);
            }
        }

        
    });
    
    
    
});