#!/bin/bash

# Lista de controladores a actualizar
CONTROLLERS=(
    "src/main/java/com/school/web/controller/admin/ParentController.java"
    "src/main/java/com/school/web/controller/admin/StaffController.java"
    "src/main/java/com/school/web/controller/academic/CourseController.java"
    "src/main/java/com/school/web/controller/academic/GradeController.java"
    "src/main/java/com/school/web/controller/infra/AssetController.java"
    "src/main/java/com/school/web/controller/finance/FinanceController.java"
)

for controller in "${CONTROLLERS[@]}"; do
    if [ -f "$controller" ]; then
        echo "Actualizando $controller..."
        
        # Agregar imports necesarios
        sed -i '/^import/a import com.school.core.controller.BaseDeleteController;\nimport com.school.core.validation.ValidId;\nimport jakarta.servlet.http.HttpServletRequest;\nimport org.springframework.validation.annotation.Validated;' "$controller"
        
        # Extender BaseDeleteController
        sed -i 's/public class \([A-Za-z]*Controller\)/public class \1 extends BaseDeleteController/' "$controller"
        
        # Agregar @Validated
        sed -i '/^@Controller/a @Validated' "$controller"
        
        echo "✓ $controller actualizado"
    else
        echo "✗ $controller no encontrado"
    fi
done

echo "Aplicación masiva completada"