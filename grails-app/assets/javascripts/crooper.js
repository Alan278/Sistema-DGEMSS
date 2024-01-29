var cropper;

function getCroppedCanvas(sourceCanvas) {
    var canvas = document.createElement('canvas');
    var context = canvas.getContext('2d');
    var width = sourceCanvas.width;
    var height = sourceCanvas.height;

    canvas.width = width;
    canvas.height = height;
    context.imageSmoothingEnabled = true;

    context.globalCompositeOperation = "source-over";
    context.beginPath();

    context.fillStyle = "white";
    context.fillRect(0, 0, canvas.width, canvas.height);

    context.drawImage(sourceCanvas, 0, 0, width, height);
    context.globalCompositeOperation = 'destination-in';
    context.beginPath();
    context.fill();

    context.globalCompositeOperation = "source-over";
    context.beginPath();

    context.rect(0, 0, width, height);

    return canvas;
}

function getRoundedCanvas(sourceCanvas) {
    var canvas = document.createElement('canvas');
    var context = canvas.getContext('2d');
    var width = sourceCanvas.width;
    var height = sourceCanvas.height;

    canvas.width = width;
    canvas.height = height;
    context.imageSmoothingEnabled = true;
        
    context.globalCompositeOperation = "source-over";
    context.beginPath();
        
    context.fillStyle = "red";
    context.fillRect(0, 0, canvas.width, canvas.height);
        
    context.drawImage(sourceCanvas, 0, 0, width, height);
    context.globalCompositeOperation = 'destination-in';
    context.beginPath();

    var centroX = width/2, centroY = height/2, radioX = width/2, radioY = height/2, rotacion=0, ap = 0, af = 2*Math.PI, cR = true;
    context.ellipse(centroX, centroY, radioX, radioY, rotacion, ap, af, cR);

    context.fill();
        
    var margin = 1;
    var centroX = width/2, centroY = height/2, radioX = (width/2) -margin, radioY = (height/2) -margin, rotacion=0, ap = 0, af = 2*Math.PI, cR = true;
        
    context.globalCompositeOperation = "source-over";
    context.beginPath();
        
    context.ellipse(centroX, centroY, radioX, radioY, rotacion, ap, af, cR);
        
        
    context.lineWidth = 2;
    context.strokeStyle="#d0d0d0";
    context.stroke();
        
    return canvas;
}

function crop() {
    var base64 = null;

    if (cropper) {
        var croppedCanvas = cropper.getCroppedCanvas({
            width: 661,
            height: 944
        });

        var croppedCanvas = getCroppedCanvas(croppedCanvas);
        var roundedCanvas = getRoundedCanvas(croppedCanvas);

        var dataUrl = roundedCanvas.toDataURL();
        $("#fotografiaRecortada").attr("src", dataUrl);

        base64 = dataUrl.replace(/^data:image\/(png|jpg);base64,/, "");
        console.log(base64);
    }

    return base64;
}

function moverIzquierda(){
    cropper.move(-5,0);
}

function moverDerecha(){
    cropper.move(5,0);
}

function moverArriba(){
    cropper.move(0,-5);
}

function moverAbajo(){
    cropper.move(0,5);
}

function acercar(){
    cropper.zoom(0.05);
}

function alejar(){
    cropper.zoom(-0.05);
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
                aspectRatio: 0.7,
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