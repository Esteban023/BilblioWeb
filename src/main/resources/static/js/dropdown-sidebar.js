document.addEventListener("DOMContentLoaded", function () {
    document
        .querySelectorAll('[data-toggle="mi-panel"]')
        .forEach((toggle) => {
            const menu = toggle
                .closest(".dropdown-cont")
                .querySelector('[data-menu="mi-panel"]');
            const arrow = toggle.querySelector('[data-arrow="mi-panel"]');

            if (!menu || !arrow) return;

            const abrir = () => {
                menu.style.display = "block";
                toggle.setAttribute("aria-expanded", "true");
                arrow.style.transform = "rotate(180deg)";
            };

            const cerrar = () => {
                menu.style.display = "none";
                toggle.setAttribute("aria-expanded", "false");
                arrow.style.transform = "rotate(0deg)";
            };

            toggle.addEventListener("click", () => {
                menu.style.display === "block" ? cerrar() : abrir();
            });

            toggle.addEventListener("keydown", (e) => {
                if (e.key === "Enter" || e.key === " ") {
                    e.preventDefault();
                    toggle.click();
                }
            });
        });

        //listener para checkbox del filtro categoria
        document.querySelectorAll('input[name="categoriaOpcFilter"]').forEach(cb =>{
            cb.addEventListener("change", () => {
                const input = document.getElementById("categoriaFormBusq")
                enviarForm(cb, input)
                
            });
        });

        //listener para checkbox de filtro autores
        document.querySelectorAll('input[name="autorOpcFilter"]').forEach(cb =>{
            cb.addEventListener("change", () => {
                const input = document.getElementById("autorFormBusq")
                enviarForm(cb, input)
            })
        })

        //listener para checkbox de filtro ISBN
        const cbISBN = document.getElementById("filterISBN")
        cbISBN.addEventListener("change", () =>{
            const input = document.getElementById("isbnFormBusq")
            enviarForm(cbISBN, input)
        })

        //listener para responsive del sidebar
        const toggleBtns = document.querySelectorAll('.side-toggle');
        const sidebar = document.querySelector('.side-bar');
        toggleBtns.forEach(toggleBtn =>{
            toggleBtn.addEventListener('click', () => {
                sidebar.classList.toggle('side-active'); // alterna entre mostrar/ocultar
            });
        })

});
document.body.addEventListener("htmx:afterSwap", function(evt) {
    // Re-enganchar listeners a los nuevos checkboxes
    document.querySelectorAll('input[name="autorOpcFilter"]').forEach(cb => {
      cb.addEventListener("change", () => {
        const input = document.getElementById("autorFormBusq");
        console.log(cb.checked)
        enviarForm(cb, input);
      });
    });
  
});

//solo un checkbox seleccionado
function soloUno(checkbox) {
    const checkboxes = document.getElementsByName(checkbox.name);
    checkboxes.forEach((item) => {
        if (item !== checkbox) item.checked = false;
    });
}

//procesar el checkbox para el envio del formulario
function enviarForm(cb, input) {
    if (cb.checked) {
        soloUno(cb)
        input.value = cb.value
    } else {
        input.value = "TODOS"
    }
    const form = document.getElementById("busqueda-principal-form")
    if (document.getElementById("busqueda-principal").value !== "") {
        // Verifica si el form tiene atributos htmx
        if (form.hasAttribute("hx-post")) {
            // Usa htmx
            htmx.trigger(form, "submit");
        } else {
            // Usa submit clásico
            form.submit();
        }
    }
}