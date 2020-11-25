#Instructions: 

Usage:`./convert.sh \[Markdown file or directory to convert\] \[The target directory of the generated html file\] \[The image resources that markdown files may need\]`

Run the convert.sh script from the liferay-docs directory or any subdirectory.

The first argument is the path to the Markdown file to convert to HTML.
The second argument is optional. It specifies the path to the HTML file to be created. If this argument is omitted, the HTML file to be created is created in the same directory as the Markdown file and has the same filename as the Markdown file except that the .markdown file extension is replaced by the .html file extension.

The third argument is optional. It specifies a folder with Image resources referenced by the Image link appears in Markdown file.It will copy the folder under the target directory you specify in second argument, and the Image Link in generated HTML files will reference these images.If this argument is omitted, it won't copy image resources unless the relative image link is represent the correct image file.

#Notes

The markdown converter cli jar was built from this branch: https://github.com/gamerson/liferay-community-site/tree/markdownConverterCli