import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream
import org.apache.tools.tar.TarOutputStream

import java.nio.file.Files
import java.nio.file.Paths

def testGz(){
    def is = Files.newInputStream(Paths.get("archive.tar"));
    OutputStream fout = Files.newOutputStream(Paths.get("archive.tar.gz"));
    BufferedOutputStream out = new BufferedOutputStream(fout);
    GzipCompressorOutputStream gzOut = new GzipCompressorOutputStream(out);
    final byte[] buffer = new byte[buffersize];
    int n = 0;
    while (-1 != (n = is.read(buffer))) {
        gzOut.write(buffer, 0, n);
    }
    gzOut.close();
    is.close();
}

def testTar(){
    def name = "D:/xx"
    def tarOutput = new TarArchiveOutputStream(new File("d:/test-tar.tar").newOutputStream())
    def file = new File("d:/xx.log")
    TarArchiveEntry entry = new TarArchiveEntry(file)
    entry.setSize(file.bytes.length)
    tarOutput.putArchiveEntry(entry)
    tarOutput.write(file.bytes)
    tarOutput.closeArchiveEntry()
    tarOutput.close()
}
//testTar()

tar(new File("D:/xx"),["WEB-INF/lib"])

def addFileToTar(TarArchiveOutputStream tarOutput,File file,Set ignoreFileAbsPaths,String nameBase){
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
def tar(File file,ignoreFiles){
    def tarOutput = new TarArchiveOutputStream(new File(file.absolutePath+".tar").newOutputStream())
    tarOutput.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
    def ignoreFileAbsPaths = ignoreFiles.collect{
        new File(file,it).absolutePath
    } as HashSet
    addFileToTar(tarOutput,file,ignoreFileAbsPaths,"")
    tarOutput.close()
}

