
package net.jnwd.origamiData;

public class Designer {
    public Long id;
    public String name;

    public Designer(String name) {
        this.name = name;
    }

    public Designer(Model model) {
        name = model.getCreator();
    }

    public boolean equals(Object that) {
        Designer cThat;

        if (that instanceof Designer) {
            cThat = (Designer) that;
        } else {
            return false;
        }

        if (!cThat.name.equals(name)) {
            return false;
        }

        return true;
    }
}
