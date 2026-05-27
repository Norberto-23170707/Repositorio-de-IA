import splitfolders

# Esta librería toma tu Dataset_Procesado y lo divide limpiamente
# 70% para entrenar, 15% para validar, 15% para pruebas finales.
splitfolders.ratio("dataset", 
                   output="Dataset_Final", 
                   seed=42, 
                   ratio=(.7, .15, .15), 
                   group_prefix=None)

print("Dataset dividido con éxito en la carpeta 'Dataset_Final' (Train, Val, Test).")