class FormValidator {
    static validateId(id) {
        return id && id !== 'not-set' && id !== 'null' && id.trim() !== '' && 
               !isNaN(parseInt(id)) && parseInt(id) > 0;
    }

    static showError(message) {
        const alert = document.createElement('div');
        alert.className = 'alert alert-danger alert-dismissible fade show';
        alert.innerHTML = `
            <i class="fas fa-exclamation-triangle"></i> ${message}
            <button type="button" class="close" data-dismiss="alert">
                <span>&times;</span>
            </button>
        `;
        document.querySelector('.container-fluid').prepend(alert);
        setTimeout(() => alert.remove(), 5000);
    }

    static interceptDeleteAction(url) {
        const idMatch = url.match(/\/delete\/(.+)$/);
        if (!idMatch || !this.validateId(idMatch[1])) {
            this.showError('ID inválido. No se puede proceder con la eliminación.');
            return false;
        }
        return true;
    }

    static confirmAction(message = '¿Está seguro de realizar esta acción?') {
        return confirm(message);
    }
}

// Función global mejorada
function confirmDelete(url, entityName = 'elemento') {
    if (!FormValidator.interceptDeleteAction(url)) {
        return;
    }
    
    if (!FormValidator.confirmAction(`¿Está seguro de eliminar este ${entityName}?`)) {
        return;
    }
    
    const form = document.getElementById('globalDeleteForm');
    if (form) {
        form.action = url;
    } else {
        FormValidator.showError('Error del sistema: Formulario no encontrado');
    }
}

// Prevenir doble clic en botones de eliminación
document.addEventListener('DOMContentLoaded', function() {
    const deleteButtons = document.querySelectorAll('[data-action="delete"]');
    deleteButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            if (this.disabled) {
                e.preventDefault();
                return false;
            }
            this.disabled = true;
            setTimeout(() => this.disabled = false, 2000);
        });
    });
});