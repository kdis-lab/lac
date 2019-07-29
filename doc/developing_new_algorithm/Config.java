 package lac.algorithms.mc;

 public class Config extends lac.algorithms.Config {
     private Boolean majority = true;
    
     public void setMajority(Boolean majority) {
         this.majority = majority;
     }
     
     public Boolean getMajority() {
         return this.majority;
     }
 } 