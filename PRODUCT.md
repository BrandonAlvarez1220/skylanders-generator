# Product

<!-- impeccable:product-schema 1 -->

## Platform

android

## Stack

Kotlin nativo para Android.

## Users

Jugadores y coleccionistas de Skylanders que quieren seguir usando en el juego personajes cuyos juguetes físicos dejaron de venderse. El dispositivo de referencia para las primeras pruebas es un Samsung Galaxy S25 Ultra.

## Product Purpose

Crear una app Android que permita leer datos NFC de Skylanders y preparar tarjetas NFC físicas para usarlas en el portal del juego. El primer juego elegido para las pruebas es Skylanders Giants. El éxito depende de que el teléfono pueda escribir la tarjeta adecuada y de que el juego la reconozca en el portal; esa compatibilidad todavía debe demostrarse.

## Positioning

La propuesta es usar tarjetas NFC físicas como soporte portátil para personajes de Skylanders, en lugar de depender de tener cada figura física. El formato de tarjeta y el flujo técnico que hacen posible esta experiencia aún están por validar.

## Operating Context

- Seleccionar o leer un personaje desde un teléfono Android con NFC.
- Escribir una tarjeta NFC física y colocarla sobre el portal de Skylanders Giants.
- Teléfono de referencia: Samsung Galaxy S25 Ultra.
- La carpeta `Skylandeers Ultimate NFC Pack/Skylandeer's Ultimate NFC Pack/` contiene archivos `.dump`, una guía, herramientas Mifare para Windows y artes de tarjetas.

## Capabilities and Constraints

- La app debe leer y escribir personajes en etiquetas/tarjetas NFC.
- Skylanders Giants es el primer objetivo de compatibilidad.
- El usuario cree que las tarjetas pueden ser MIFARE, pero todavía no tiene tarjetas para probarlo; tipo de chip, capacidad, lector y compatibilidad del teléfono son decisiones abiertas.
- Una guía comunitaria describe los tags de Skylanders como MIFARE S50/1K; este dato aún no se ha comprobado contra los dumps locales ni con una tarjeta real ([referencia](https://skylandersnfc.github.io/Docs/Skylanders_NFC_Explained)).
- Android documenta que el soporte `MifareClassic` es opcional por dispositivo, por lo que la compatibilidad del Samsung Galaxy S25 Ultra debe probarse ([documentación oficial](https://developer.android.com/reference/android/nfc/tech/MifareClassic)).
- El teléfono se usaría para leer/escribir la tarjeta; el juego necesita su Portal of Power. El portal de Giants se describe como cableado, pero la compatibilidad exacta depende de consola y portal ([referencia](https://retrodeck.readthedocs.io/en/latest/wiki_controllers/toystolife/skylanders-portal-of-power)).
- El objetivo de prueba es Skylanders Giants para Xbox 360; el usuario jugará en Xbox Series S/X mediante retrocompatibilidad. La combinación exacta de consola, portal y juego debe probarse físicamente.
- La compatibilidad de los archivos `.dump` incluidos con el flujo de escritura de Android aún debe verificarse.

## Brand Commitments

La interfaz debe sentirse acorde con Skylanders y verse pulida. No se ha fijado una paleta, tipografía ni uso específico de logotipos o arte oficial.

## Evidence on Hand

- La carpeta `Skylandeers Ultimate NFC Pack/Skylandeer's Ultimate NFC Pack/` incluye archivos `.dump` organizados en directorios de generaciones y personajes de Skylanders, además de imágenes de tarjetas.
- La misma carpeta incluye `HELPFUL GUIDE (READ ME).pdf` y dos instaladores de Mifare Windows Tool.
- La documentación oficial de Android indica que el soporte MIFARE Classic depende del dispositivo; las referencias comunitarias y de compatibilidad de portales son pistas iniciales, no pruebas con el S25 Ultra ni con el portal del usuario.
- Existe un MVP inicial Android en Kotlin para el catálogo Giants y el diagnóstico NFC de solo lectura; aún no se ha compilado ni probado en el teléfono.
- No hay teléfono de prueba conectado ni tarjetas NFC en el equipo.
- La cobertura e integridad de los dumps y la guía aún no se han auditado; no se debe afirmar que el paquete contiene todos los personajes hasta completar ese inventario.

## Product Principles

- Verificar en hardware real que una tarjeta escrita sea legible por el portal antes de declarar compatibilidad.
- Conservar los dumps originales y evitar escrituras accidentales o irreversibles.
- Mostrar claramente el personaje, la tarjeta de destino y el resultado de cada lectura o escritura.
