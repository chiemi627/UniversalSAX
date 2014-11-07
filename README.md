# UniversalSAX
This product encodes the time sequence dataset to charactor string by using UniversalSAX technology. The detail of techniques is written in the following paper. (I am sorry that the paper is written in Japanese.)
* 大西史花, 渡辺知恵美: UniversalSAX: 空間充填曲線を利用したSAXの多次元時系列データへの適用, DEIM Forum 2012, D4-4
* http://db-event.jpn.org/deim2012/proceedings/final-pdf/d4-4.pdf

## Usage:

* Generate index file
> % java uSaxLabeler &lt;dimension&gt; &lt;resolution&gt; &lt;labels&gt;  &lt;block_resolution&gt;

 * Arguments
   * dimension : dimension of the data. Default values is 3.
   * resolution : the number of cells on each dimension. 
it is described as $ 2^{resolution} $ Default value is 9. It mean that each dimension has $2^9$ cells.
   * labels : the number of labels. Default value is 2048.
   * block_resolution : Please refer the paper about block resolution because it is very difficult to explain here... Default number is 6.
 
 * Output
    * The index file is outputed as 
    "LabelInfo_&lt;dimension&gt;_&lt;resolution&gt;_&lt;labels&gt;_.txt"

* Encode time sequence datasets
 * Prepare
   * The time sequence data should be saved as CSV file.
   * Create a directory and put all time sequence datas files in the directory
 * Command 
   > % java SaxIndexGenerator <dim> <res> <labels> <window> <dir>
   
 * Arguments
   * dim : dimension of the data
   * res : resolution of the SAX lattice
   * labels : number of labels for Universal SAX
   * windows : the length of the time interval for smoothing the time sequence.
 
 * Output

    The encoded strings are saves as a file named    "result_USAX_&lt;dim&gt;_&lt;dim&gt;_&lt;labels&gt;_.csv"









