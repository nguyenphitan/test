const header = document.getElementById('header');
const mobileMenu = document.querySelector('#header .mobile-menu-btn');
const headerHeight = header.clientHeight;
// open / close menu
mobileMenu.addEventListener('click', () => {
    let isClose = header.clientHeight === headerHeight;
    if(isClose) {
        header.style.height = 'auto';
    }
    else {
        header.style.height = null;
    }

})


const menuItems = document.querySelectorAll('#nav li a[href*="#"]');

for(let i in menuItems) {
    let mItem = menuItems[i];
    mItem.onclick = function(event) {
        let isParentMenu = this.nextElementSibling && this.nextElementSibling.classList.contains('subnav');
        if(isParentMenu) {
            event.preventDefault();
        }
        else {
            header.style.height = null;
        }
    }
}


