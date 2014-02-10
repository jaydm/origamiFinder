
package net.jnwd.origamiData;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CreateDBScripts {
    private String deQuote(String data) {
        String work = data;

        if (work.charAt(0) == '\"') {
            work = work.substring(1);
        }

        if (work.charAt(work.length() - 1) == '\"') {
            work = work.substring(0, (work.length() - 1));
        }

        return work;
    }

    private void loadData() throws IOException {
        InputStream inputStream = new FileInputStream(
                "/home/jaydm/git/origamiCentral/xml/model.raw");

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            String line;

            List<Source> sources = new ArrayList<Source>();

            // List<Author> authors = new ArrayList<Author>();

            List<Designer> designers = new ArrayList<Designer>();

            List<Model> allModels = new ArrayList<Model>();

            long nextSourceID = 1;

            // long nextAuthorID = 1;

            long nextDesignerID = 1;

            while ((line = reader.readLine()) != null) {
                System.out.println("Read line: " + line);

                Model model = new Model(line);

                Source source = new Source(model);

                if (sources.contains(source)) {
                    for (Source test : sources) {
                        if (test.equals(source)) {
                            source.id = test.id;

                            break;
                        }
                    }
                } else {
                    System.out.println("Creating new source: " + source.title);

                    source.id = nextSourceID++;

                    sources.add(source);
                }

                model.sourceID = source.id;

                Designer designer = new Designer(model);

                if (designers.contains(designer)) {
                    for (Designer test : designers) {
                        if (test.equals(designer)) {
                            designer.id = test.id;

                            break;
                        }
                    }
                } else {
                    System.out.println("Creating new designer: " + designer.name);

                    designer.id = nextDesignerID++;

                    designers.add(designer);
                }

                model.creatorID = designer.id;

                if (!allModels.contains(model)) {
                    allModels.add(model);
                }
            }

            System.out.println("Creating sql scripts...");

            String newLine = System.getProperty("line.separator");
            String sql;

            FileWriter sourceScript = new FileWriter(
                    "/home/jaydm/git/origamiCentral/xml/sources.sql");

            sourceScript.write("delete from source" + newLine);

            for (Source source : sources) {
                sql = "" +
                        "insert into source (id, source_title, source_isbn) " +
                        "values (" +
                        source.id + ", " +
                        "'" + deQuote(source.title) + "', " +
                        "'" + deQuote(source.isbn) + "')";

                System.out.println(sql);

                sourceScript.write(sql + newLine);
            }

            sourceScript.close();

            FileWriter designerScript = new FileWriter(
                    "/home/jaydm/git/origamiCentral/xml/designers.sql");

            designerScript.write("delete from designer" + newLine);

            for (Designer designer : designers) {
                sql = "" +
                        "insert into designer (id, designer_name) " +
                        "values (" +
                        designer.id + ", " +
                        "'" + deQuote(designer.name) + "')";

                System.out.println(sql);

                designerScript.write(sql + newLine);
            }

            designerScript.close();

            FileWriter modelScript = new FileWriter("/home/jaydm/git/origamiCentral/xml/models.sql");

            modelScript.write("delete from model" + newLine);

            for (Model model : allModels) {
                sql = ""
                        +
                        "insert into model (id, model_name, source_id, source_name, designer_id, designer_name, model_page) "
                        +
                        "values (" +
                        model.id + ", " +
                        "'" + deQuote(model.name) + "', " +
                        model.sourceID + ", " +
                        "'" + deQuote(model.bookTitle) + "', " +
                        model.creatorID + ", " +
                        "'" + deQuote(model.creator) + "', " +
                        model.page + ")";

                System.out.println(sql);

                modelScript.write(sql + newLine);
            }

            modelScript.close();
        } finally {
            reader.close();
        }
    }

    public static void main(String[] args) {
        CreateDBScripts x = new CreateDBScripts();

        try {
            x.loadData();
        } catch (Exception e) {

        }
    }

}
