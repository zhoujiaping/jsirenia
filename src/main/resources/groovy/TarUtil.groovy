import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream

class TarUtil {

    private static addFileToTar(TarArchiveOutputStream tarOutput, File file, Set ignoreFileAbsPaths, String nameBase){
        if(ignoreFileAbsPaths.contains(file.absolutePath)){
            return null
        }
        if(file.isFile()){
            def entry = new TarArchiveEntry(file,nameBase+"/"+file.name)
            def bytes = file.bytes
            entry.setSize(bytes.length)
            tarOutput.putArchiveEntry(entry)
            tarOutput.write(bytes)
            tarOutput.closeArchiveEntry()
        }else{
            file.eachFile {
                subFile->
                    if(file.isDirectory()){
                        addFileToTar(tarOutput,subFile,ignoreFileAbsPaths,nameBase+"/"+file.name)
                    }
            }
        }
    }
    def static tar(File file,ignoreFiles){
        def tarOutput = new TarArchiveOutputStream(new File(file.absolutePath+".tar").newOutputStream())
        tarOutput.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
        def ignoreFileAbsPaths = ignoreFiles.collect{
            new File(file,it).absolutePath
        } as HashSet
        addFileToTar(tarOutput,file,ignoreFileAbsPaths,"")
        tarOutput.close()
    }

}
