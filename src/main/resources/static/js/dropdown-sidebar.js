document.addEventListener("DOMContentLoaded", function () {
    document
        .querySelectorAll('[data-toggle="mi-panel"]')
        .forEach((toggle) => {
            const menu = toggle
                .closest(".mipanel-section")
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
});