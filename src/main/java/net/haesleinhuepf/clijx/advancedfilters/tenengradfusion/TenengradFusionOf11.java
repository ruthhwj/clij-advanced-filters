package net.haesleinhuepf.clijx.advancedfilters.tenengradfusion;

import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import org.scijava.plugin.Plugin;

@Plugin(type = CLIJMacroPlugin.class, name = "CLIJx_tenengradFusionOf11")
public class TenengradFusionOf11 extends AbstractTenengradFusion {

    @Override
    public String getParameterHelpText() {
        return getParameterHelpText(11);
    }


}
