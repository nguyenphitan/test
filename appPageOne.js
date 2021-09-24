// Header
// Navbar
const bar = document.querySelector('.bar__icon');
const navOverlay = document.querySelector('.nav__overlay'); 
const overlay = document.querySelector('.overlay');
bar.onclick = function() {
    navOverlay.style.transform = 'translateX(0%)';
    bar.style.display = 'none';
    overlay.style.display = 'block';
}

// Menu
const closeMenu = document.querySelector('.nav__overlay__menu .fa-chevron-left');
closeMenu.onclick = function() {
    navOverlay.style.transform = 'translateX(-100%)';
    bar.style.display = 'block';
    overlay.style.display = 'none';
}

// Overlay
overlay.onclick = function() {
    navOverlay.style.transform = 'translateX(-100%)';
    bar.style.display = 'block';
    overlay.style.display = 'none';
}

// slider
const slider = document.querySelector('.home__slider');
const imgSlider = document.querySelector('.home__slider #carouselExampleIndicators .carousel-inner');

imgSlider.addEventListener('onmousemove', function(event) {
    let x = event.clientX;
    let y = event.clientY;
    let coor = "Coordinates: (" + x + "," + y + ")";
    console.log(coor);
})

slider.onclick = function() {

}







