
# Proyecto de Validación y Exportación de Expresiones Regulares

## Descripción
Este proyecto está diseñado para analizar y validar expresiones regulares, construir árboles de expresión, y exportar los resultados a archivos Excel. Adicionalmente, permite la validación de archivos TXT que contienen gramáticas y tokens definidos por el usuario.

## Estructura del Proyecto
El proyecto está compuesto por las siguientes clases principales:

- **ExpresionRegularToken.java**: Maneja los tokens en las expresiones regulares y calcula los conjuntos `first`, `last`, y `follow`.
- **ExpressionTreeExcelExporter.java**: Exporta los conjuntos `follow` a un archivo Excel.
- **ExpressionTreeParser.java**: Analiza expresiones regulares y construye un árbol de expresión.
- **ExpressionTreePrinter.java**: Proporciona una representación visual del árbol de expresión.
- **TreeNode.java**: Define los nodos en el árbol de expresión.
- **ValidacionDeExpresiones.java**: Valida la estructura de las expresiones en función de reglas gramaticales.
- **ValidadorGramatica.java**: Verifica que las gramáticas en los archivos cumplan con las estructuras definidas.

## Estructuras Utilizadas
- **Árbol de Expresión**: Representa la jerarquía de operadores y operandos en una expresión regular.
- **Mapas y Conjuntos**: Se utilizan para calcular y gestionar los conjuntos `first`, `last`, y `follow`.

## Entrada
- **Expresiones Regulares**: Se aceptan como cadenas o archivos TXT.
- **Archivos TXT**: Archivos con gramáticas y definiciones de tokens.

## Salidas
- **Árbol de Expresión**: Se puede visualizar el árbol generado a partir de una expresión regular.
- **Archivo Excel**: Exporta los resultados del análisis de las expresiones, como los conjuntos `follow`, a un archivo Excel.
- **Errores Detallados**: Si se encuentran errores en las expresiones o gramáticas, se reportan con la ubicación exacta (línea y columna).

## Requisitos del Sistema
- **Java 8+**
- Librería para manejar archivos Excel (ej. Apache POI)
- Herramienta de compilación como Maven o Gradle para gestionar dependencias

## Instrucciones de Uso

1. **Clonar el repositorio**:
   ```bash
   git clone <URL_DEL_REPOSITORIO>
   cd <NOMBRE_DEL_REPOSITORIO>
   ```

2. **Compilar el proyecto**:
   Si estás usando Maven:
   ```bash
   mvn clean install
   ```

3. **Ejecutar el programa**:
   ```bash
   java -jar target/nombre-del-archivo-jar.jar
   ```

4. **Proporcionar archivos de entrada**:
   Los archivos TXT con definiciones de gramáticas deben estar en el formato correcto para ser validados.

5. **Exportar a Excel**:
   Los resultados de la validación pueden ser exportados a Excel automáticamente.

## Contribuciones
Si deseas contribuir, por favor realiza un fork del proyecto y envía un pull request con tus cambios.

## Licencia
Este proyecto está licenciado bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.
