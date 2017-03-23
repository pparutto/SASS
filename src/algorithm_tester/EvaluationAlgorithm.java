/*
 * Copyright (C) 2017 Laboratory of Experimental Biophysics
 * Ecole Polytechnique Federale de Lausanne
 *
 * Author: Marcel Stefko
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package algorithm_tester;

import ij.ImageStack;
import java.util.HashMap;
/**
 *
 * @author stefko
 */



public interface EvaluationAlgorithm {
    
    public void setImageStack(ImageStack stack );
    
    public void processStack();
    
    public void setCustomParameters(HashMap<String, Integer> map);
    
    public HashMap<String, Integer> getCustomParameters();
    
    public HashMap<String, Double> getOutputValues(int image_no);
    
    public String getName();
    
}