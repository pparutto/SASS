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
package ch.epfl.leb.sass.simulator;

import ch.epfl.leb.sass.logging.Message;
import ch.epfl.leb.sass.models.Microscope;
import ch.epfl.leb.sass.utils.images.ImageS;
import ch.epfl.leb.sass.utils.images.ImageShapeException;

import com.google.gson.JsonElement;

import java.io.File;
import java.util.List;
import java.util.HashMap;

/**
 * The interface that defines everything that a Simulator should do.
 * 
 * @author Marcel Stefko
 * @author Kyle M. Douglass
 */
public interface Simulator {
    
    /**
     * Returns currently set control signal of the generator (e.g. laser power 
     * settings).
     * @return control signal value
     */
    public double getControlSignal();
    
    /**
     * Returns the name of the JSON key for the camera info.
     * 
     * @return The name of the key indicating the camera information.
     * @see #toJsonState()
     */
    public String getCameraJsonName();
    
    /**
     * Returns the name of the JSON key for the fluorescence state info.
     * 
     * @return The name of the key indicating the fluorescence information.
     * @see #toJsonState()
     */
    public String getFluorescenceJsonName();
    
    /**
     * Returns the name of the JSON key for the laser state info.
     * 
     * @return The name of the key indicating the laser information.
     * @see #toJsonState()
     */
    public String getLaserJsonName();
    
    /**
     * Returns the size of the field-of-view in object space units.
     * 
     * @return size of current FOV in object space units.
     */
    public double getFOVSize();
    
    /**
     * Returns the unique ID assigned to this simulator.
     * 
     * @return The integer ID of this simulator.
     */
    public int getId();
    
    /**
     * Returns the number of images simulated.
     * 
     * Because the simulation can advance without generating an image, this
     * value will be less than or equal to the number of simulation time steps.
     * Use {@link #incrementTimeStep()} to advance the simulation one time step
     * without generating an image.
     * 
     * @return The number of images that have been simulated.
     */
    public int getImageCount();
    
    /**
     * Returns messages about changes in the simulation state.
     * 
     * Unlike {@link #getSimulationState() getSimulationState()}, which returns
     * information about the *current* state of the simulation, this method
     * returns the messages from individual components that contain information
     * about changes in their state that have occurred since the last time this
     * method was called.
     * 
     * @return A list containing the state change messages.
     */
    public List<Message> getMessages();
    
    /**
     * Returns a copy of the Microscope that is controlled by this simulation.
     * 
     * The copy that is returned is a deep copy of the
     * {@link ch.epfl.leb.sass.models.Microscope Microscope} that the simulation
     * was initialized with.
     * 
     * @return A copy of the Microscope object controlled by this simulation.
     */
    public Microscope getMicroscope();

    /**
     * Generates a new image and adds it to the internal stack.
     * @return newly generated image
     */
    public ImageS getNextImage() throws ImageShapeException;
    
    /**
     * Returns the name of the JSON key for the objective state info.
     * 
     * @return The name of the key indicating the objective information.
     * @see #toJsonState()
     */
    public String getObjectiveJsonName();
    
    /**
     * Returns the size of a pixel in object space units.
     * 
     * The units of this quantity are determined by those of the camera's
     * pixels. The value is the magnitude of the camera's pixel size divided by
     * the objective's magnification.
     * 
     * @return length of one pixel side in object space units.
     */
    public double getObjectSpacePixelSize();
    
    /**
     * Returns a brief description of the ground truth signal.
     * 
     * @return A short description of the truth signal, typically its units.
     */
    public String getShortTrueSignalDescription();
    
    /**
     * Returns internal stack with all generated images.
     * 
     * @return internal stack
     */
    public ImageS getStack();
    
    /**
     * Returns the name of the JSON key for the stage state info.
     * 
     * @return The name of the key indicating the stage information.
     * @see #toJsonState()
     */
    public String getStageJsonName();
    
    /**
     * Returns the actual value of signal (if applicable) for given image.
     * @param image_no 1-based image number in history
     * @return value of signal (e.g. no. of active emitters)
     */
    public double getTrueSignal(int image_no);
    
    /**
     * Increments the simulation by one time step without creating an image.
     */
    public void incrementTimeStep();
    
    /**
     * Sets control signal of the generator (e.g. laser power). This should be
     * used by the controller.
     * @param value new value of the control signal
     */
    public void setControlSignal(double value);
    
    /**
     * Sets custom parameters of the generator.
     * @param map map of custom parameters
     */
    public void setCustomParameters(HashMap<String,Double> map);
    
    /**
     * Returns custom parameters of the generator.
     * @return map of custom parameters
     */
    public HashMap<String,Double> getCustomParameters();

    /**
     * Saves the messages in the cache to a select file.
     * 
     * @param file The file to save to.
     */
    public void saveMessages(File file);
    
    /**
     * Saves the .tif image stack to a select file.
     * @param file file to save to
     */
    public void saveStack(File file);
    
    /**
     * Saves the current state of the simulation.
     * 
     * @param file The file to save to.
     */
    public void saveState(File file);
    
    /**
     * Returns messages about changes in the simulation state as a JSON object.
     * 
     * Unlike {@link #toJsonState() toJsonState()}, which returns
     * information about the *current* state of the simulation, this method
     * returns the messages from individual simulation components that contain
     * information about changes in their state that have occurred since the
     * last time this method was called.
     * 
     * @return A JSON object containing the simulation messages.
     */
    public JsonElement toJsonMessages();
    
    /**
     * Returns information on the simulation's current state as a JSON object.
     * 
     * Unlike {@link #toJsonMessages() toJsonMessages()}, which returns
     * information about previous changes in the simulation's state, this method
     * reports on the current state of the simulation.
     * 
     * @return A JSON object containing information on the simulation state.
     */
    public JsonElement toJsonState();
}
